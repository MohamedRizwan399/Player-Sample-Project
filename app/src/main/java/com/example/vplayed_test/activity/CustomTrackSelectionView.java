package com.example.vplayed_test.activity;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.Nullable;

import com.example.vplayed_test.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider;
import com.google.android.exoplayer2.ui.TrackNameProvider;
import com.google.android.exoplayer2.util.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CustomTrackSelectionView extends LinearLayout {
    public interface TrackSelectionListener {

        /**
         * Called when the selected tracks changed.
         *
         * @param isDisabled Whether the renderer is disabled.
         * @param overrides  List of selected track selection overrides for the renderer.
         */
        void onTrackSelectionChanged(boolean isDisabled, List<DefaultTrackSelector.SelectionOverride> overrides);
    }

    private final LayoutInflater inflater;
    private final CheckedTextView disableView;
    private final CheckedTextView defaultView;
    private final CustomTrackSelectionView.ComponentListener componentListener;
    private final SparseArray<DefaultTrackSelector.SelectionOverride> overrides;

    private boolean allowAdaptiveSelections;
    private boolean allowMultipleOverrides;

    private TrackNameProvider trackNameProvider;
    private CheckedTextView[][] trackViews;

    MappingTrackSelector.MappedTrackInfo mappedTrackInfo;
    private int rendererIndex;
    private TrackGroupArray trackGroups;
    private boolean isDisabled;
    @Nullable
    private Comparator<CustomTrackSelectionView.TrackInfo> trackInfoComparator;
    @Nullable
    private CustomTrackSelectionView.TrackSelectionListener listener;

    /**
     * Creates a track selection view.
     */
    public CustomTrackSelectionView(Context context) {
        this(context, null);
    }

    /**
     * Creates a track selection view.
     */
    public CustomTrackSelectionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates a track selection view.
     */
    @SuppressWarnings("nullness")
    public CustomTrackSelectionView(
            Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);

        overrides = new SparseArray<>();

        // Don't save view hierarchy as it needs to be reinitialized with a call to init.
        setSaveFromParentEnabled(false);

        TypedArray attributeArray =
                context
                        .getTheme()
                        .obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        attributeArray.getResourceId(0, 0);
        attributeArray.recycle();

        inflater = LayoutInflater.from(context);
        componentListener = new CustomTrackSelectionView.ComponentListener();
        trackNameProvider = new DefaultTrackNameProvider(getResources());
        trackGroups = TrackGroupArray.EMPTY;

        // View for disabling the renderer.
        disableView =
                (CheckedTextView)
                        inflater.inflate(R.layout.checked_layout_player, this, false);
        disableView.setText("Off");
        disableView.setEnabled(true);
        disableView.setFocusable(true);
        disableView.setOnClickListener(componentListener);
        disableView.setVisibility(View.GONE);
        addView(disableView);
        // Divider view.
        // View for clearing the override to allow the selector to use its default selection logic.
        defaultView =
                (CheckedTextView)
                        inflater.inflate(R.layout.checked_layout_player, this, false);

        defaultView.setText(R.string.exo_track_selection_auto);
        defaultView.setEnabled(false);
        defaultView.setFocusable(true);
        defaultView.setOnClickListener(componentListener);
        addView(defaultView);
    }

    /**
     * Sets whether adaptive selections (consisting of more than one track) can be made using this
     * selection view.
     *
     * <p>For the view to enable adaptive selection it is necessary both for this feature to be
     * enabled, and for the target renderer to support adaptation between the available tracks.
     *
     * @param allowAdaptiveSelections Whether adaptive selection is enabled.
     */
    public void setAllowAdaptiveSelections(boolean allowAdaptiveSelections) {
        if (this.allowAdaptiveSelections != allowAdaptiveSelections) {
            this.allowAdaptiveSelections = allowAdaptiveSelections;
            updateViews();
        }
    }

    /**
     * Sets whether tracks from multiple track groups can be selected. This results in multiple {@link
     * DefaultTrackSelector.SelectionOverride SelectionOverrides} to be returned by {@link #getOverrides()}.
     *
     * @param allowMultipleOverrides Whether multiple track selection overrides can be selected.
     */
    public void setAllowMultipleOverrides(boolean allowMultipleOverrides) {
        if (this.allowMultipleOverrides != allowMultipleOverrides) {
            this.allowMultipleOverrides = allowMultipleOverrides;
            if (!allowMultipleOverrides && overrides.size() > 1) {
                for (int i = overrides.size() - 1; i > 0; i--) {
                    overrides.remove(i);
                }
            }
            updateViews();
        }
    }

    /**
     * Sets whether an option is available for disabling the renderer.
     *
     * @param showDisableOption Whether the disable option is shown.
     */
    public void setShowDisableOption(boolean showDisableOption) {
        disableView.setVisibility(showDisableOption ? View.VISIBLE : View.GONE);
    }

    public void setShowDefaultDisableOption(boolean showDisableOption) {
        defaultView.setVisibility(showDisableOption ? View.VISIBLE : View.GONE);
    }

    /**
     * Sets the {@link TrackNameProvider} used to generate the user visible name of each track and
     * updates the view with track names queried from the specified provider.
     *
     * @param trackNameProvider The {@link TrackNameProvider} to use.
     */
    public void setTrackNameProvider(TrackNameProvider trackNameProvider) {
        this.trackNameProvider = Assertions.checkNotNull(trackNameProvider);
        updateViews();
    }

    /**
     * Initialize the view to select tracks for a specified renderer using {@link MappingTrackSelector.MappedTrackInfo} and
     * a set of {@link DefaultTrackSelector.Parameters}.
     *
     * @param mappedTrackInfo       The {@link MappingTrackSelector.MappedTrackInfo}.
     * @param rendererIndex         The index of the renderer.
     * @param isDisabled            Whether the renderer should be initially shown as disabled.
     * @param overrides             List of initial overrides to be shown for this renderer. There must be at most
     *                              one override for each track group. If {@link #setAllowMultipleOverrides(boolean)} hasn't
     *                              been set to {@code true}, only the first override is used.
     * @param trackFormatComparator An optional comparator used to determine the display order of the
     *                              tracks within each track group.
     * @param listener              An optional listener for track selection updates.
     */
    public void init(
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo,
            int rendererIndex,
            boolean isDisabled,
            List<DefaultTrackSelector.SelectionOverride> overrides,
            @Nullable Comparator<Format> trackFormatComparator,
            @Nullable CustomTrackSelectionView.TrackSelectionListener listener) {
        try {
            this.mappedTrackInfo = mappedTrackInfo;
            this.rendererIndex = rendererIndex;
            this.isDisabled = isDisabled;
            this.trackInfoComparator =
                    trackFormatComparator == null
                            ? null
                            : (o1, o2) -> trackFormatComparator.compare(o1.format, o2.format);
            this.listener = listener;
            int maxOverrides = allowMultipleOverrides ? overrides.size() : Math.min(overrides.size(), 1);
            for (int i = 0; i < maxOverrides; i++) {
                DefaultTrackSelector.SelectionOverride override = overrides.get(i);
                this.overrides.put(override.groupIndex, override);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            updateViews();
        }
    }

    /**
     * Returns whether the renderer is disabled.
     */
    public boolean getIsDisabled() {
        return isDisabled;
    }

    /**
     * Returns the list of selected track selection overrides. There will be at most one override for
     * each track group.
     */
    public List<DefaultTrackSelector.SelectionOverride> getOverrides() {
        List<DefaultTrackSelector.SelectionOverride> overrideList = new ArrayList<>(overrides.size());
        for (int i = 0; i < overrides.size(); i++) {
            overrideList.add(overrides.valueAt(i));
        }
        return overrideList;
    }

    // Private methods.

    private void updateViews() {
        // Remove previous per-track views.
        for (int i = getChildCount() - 1; i >= 3; i--) {
            removeViewAt(i);
        }

        if (mappedTrackInfo == null) {
            // The view is not initialized.
            disableView.setEnabled(false);
            defaultView.setEnabled(false);
            return;
        }
        disableView.setEnabled(true);
        defaultView.setEnabled(true);

        trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);

        // Add per-track views.
        trackViews = new CheckedTextView[trackGroups.length][];
        for (int groupIndex = 0; groupIndex < trackGroups.length; groupIndex++) {
            TrackGroup group = trackGroups.get(groupIndex);
            Format[] formats = new Format[group.length];
            for (int trackGroup = 0; trackGroup < group.length; trackGroup++) {
                formats[trackGroup] = group.getFormat(trackGroup);
            }
            Arrays.sort(formats, (o1, o2) -> o2.bitrate - o1.bitrate);
            ArrayList<Format> formatNew = new ArrayList<>();
            group = new TrackGroup(removeDuplicate(formats, 0, formatNew));
            trackViews[groupIndex] = new CheckedTextView[group.length];
            CustomTrackSelectionView.TrackInfo[] trackInfos = new CustomTrackSelectionView.TrackInfo[group.length];
            for (int trackIndex = 0; trackIndex < group.length; trackIndex++) {
                trackInfos[trackIndex] = new CustomTrackSelectionView.TrackInfo(groupIndex, trackIndex, group.getFormat(trackIndex));
            }
            if (trackInfoComparator != null) {
                Arrays.sort(trackInfos, trackInfoComparator);
            }
            for (int trackIndex = 0; trackIndex < trackInfos.length; trackIndex++) {

                int trackViewLayoutId =
                        R.layout.checked_layout_player;
                CheckedTextView trackView =
                        (CheckedTextView) inflater.inflate(trackViewLayoutId, this, false);

                if (rendererIndex == 0) {
                    Log.d("TITLE_NAME_1:::", buildTrackName(group.getFormat(trackIndex)));
                    trackView.setText(buildTrackName(group.getFormat(trackIndex)));
                } else if (rendererIndex == 1) {
                    String languages = trackInfos[trackIndex].format.language;
                    String labels = trackInfos[trackIndex].format.label;

                    if (languages == null) {
                        trackView.setText(trackNameProvider.getTrackName(trackInfos[trackIndex].format));
                    } else {
                        try {
                            Locale loc = new Locale(languages);
                            String name = loc.getDisplayLanguage(loc);
                            if (labels.equals(loc.getDisplayLanguage())) {
                                trackView.setText(name);
                            } else {
                                trackView.setText(labels);
                            }
                        } catch (Exception e) {
                            trackView.setText(trackNameProvider.getTrackName(trackInfos[trackIndex].format).replace(", Stereo", ""));
                        }

                    }

                } else {
                    trackView.setText(trackNameProvider.getTrackName(trackInfos[trackIndex].format));
                    Log.e("TITLE_NAME_3:::", trackNameProvider.getTrackName(trackInfos[trackIndex].format));
                }
                trackView.setTag(trackInfos[trackIndex]);
                if (mappedTrackInfo.getTrackSupport(rendererIndex, groupIndex, trackIndex)
                        == C.FORMAT_HANDLED) {
                    trackView.setFocusable(true);
                    trackView.setOnClickListener(componentListener);
                } else {
                    trackView.setFocusable(false);
                    trackView.setEnabled(false);
                }
                trackViews[groupIndex][trackIndex] = trackView;
                addView(trackView);
            }
        }

        updateViewStates();
    }

    private Format[] removeDuplicate(Format[] formats, int index, ArrayList<Format> formatNew) {
        if (index != formats.length && index != 0 && formats[0].height == formats[index].height) {
            return formatNew.toArray(new Format[formatNew.size()]);
        } else if (index != formats.length) {
            formatNew.add(formats[index]);
            removeDuplicate(formats, index + 1, formatNew);
        }
        return formatNew.toArray(new Format[formatNew.size()]);
    }

    public String buildTrackName(Format format) {
        String trackName = buildResolutionString(format) + "p";
        return trackName.length() == 0 ? "unknown" : trackName;
    }

    private String buildResolutionString(Format format) {
        return format.width == Format.NO_VALUE || format.height == Format.NO_VALUE
                ? "" : String.valueOf(format.height);
    }

    private void updateViewStates() {

        disableView.setChecked(isDisabled);
        defaultView.setChecked(!isDisabled && overrides.size() == 0);
        for (int i = 0; i < trackViews.length; i++) {
            DefaultTrackSelector.SelectionOverride override = overrides.get(i);
            for (int j = 0; j < trackViews[i].length; j++) {
                if (override != null) {
                    CustomTrackSelectionView.TrackInfo trackInfo = (CustomTrackSelectionView.TrackInfo) Assertions.checkNotNull(trackViews[i][j].getTag());
                    trackViews[i][j].setChecked(override.containsTrack(trackInfo.trackIndex));
                } else {
                    trackViews[i][j].setChecked(false);
                }
            }
        }
    }

    private void onClick(View view) {
        resetViewTrack();
        if (view == disableView) {
            onDisableViewClicked();
        } else if (view == defaultView) {
            onDefaultViewClicked();
        } else {
            onTrackViewClicked(view);
        }
        updateViewStates();
        if (listener != null) {
            listener.onTrackSelectionChanged(getIsDisabled(), getOverrides());
        }
    }

    private void resetViewTrack() {
        for (int i = 0; i < trackViews.length; i++) {
            for (int j = 0; j < trackViews[i].length; j++) {
                trackViews[i][j].setChecked(false);
            }
        }
    }

    private void onTrackViewClicked(View view) {
        isDisabled = false;
        CustomTrackSelectionView.TrackInfo trackInfo = (CustomTrackSelectionView.TrackInfo) Assertions.checkNotNull(view.getTag());
        int groupIndex = trackInfo.groupIndex;
        int trackIndex = trackInfo.trackIndex;
        DefaultTrackSelector.SelectionOverride override = overrides.get(groupIndex);
        Assertions.checkNotNull(mappedTrackInfo);
        if (override == null) {
            // Start new override.
            if (!allowMultipleOverrides && overrides.size() > 0) {
                // Removed other overrides if we don't allow multiple overrides.
                overrides.clear();
            }
            overrides.put(groupIndex, new DefaultTrackSelector.SelectionOverride(groupIndex, trackIndex));


        } else {
            // An existing override is being modified.
            int overrideLength = override.length;
            int[] overrideTracks = override.tracks;
            boolean isCurrentlySelected = ((CheckedTextView) view).isChecked();
            boolean isAdaptiveAllowed = shouldEnableAdaptiveSelection(groupIndex);
            boolean isUsingCheckBox = isAdaptiveAllowed || shouldEnableMultiGroupSelection();
            if (isCurrentlySelected && isUsingCheckBox) {
                // Remove the track from the override.
                if (overrideLength == 1) {
                    // The last track is being removed, so the override becomes empty.
                    overrides.remove(groupIndex);
                } else {
                    int[] tracks = getTracksRemoving(overrideTracks, trackIndex);
                    overrides.put(groupIndex, new DefaultTrackSelector.SelectionOverride(groupIndex, tracks));
                }
            } else if (!isCurrentlySelected) {
                if (isAdaptiveAllowed) {
                    // Add new track to adaptive override.
                    int[] tracks = getTracksAdding(overrideTracks, trackIndex);
                    overrides.put(groupIndex, new DefaultTrackSelector.SelectionOverride(groupIndex, tracks));
                } else {
                    // Replace existing track in override.
                    overrides.put(groupIndex, new DefaultTrackSelector.SelectionOverride(groupIndex, trackIndex));
                }
            }
        }
    }



    private void onDisableViewClicked() {
        isDisabled = true;
        overrides.clear();
    }

    private void onDefaultViewClicked() {
        isDisabled = false;
        overrides.clear();
    }



    private boolean shouldEnableAdaptiveSelection(int groupIndex) {
        return allowAdaptiveSelections
                && trackGroups.get(groupIndex).length > 1
                && mappedTrackInfo.getAdaptiveSupport(
                rendererIndex, groupIndex, /* includeCapabilitiesExceededTracks= */ false)
                != RendererCapabilities.ADAPTIVE_NOT_SUPPORTED;
    }

    private boolean shouldEnableMultiGroupSelection() {
        return allowMultipleOverrides && trackGroups.length > 1;
    }

    private static int[] getTracksAdding(int[] tracks, int addedTrack) {
        tracks = Arrays.copyOf(tracks, tracks.length + 1);
        tracks[tracks.length - 1] = addedTrack;
        return tracks;
    }

    private static int[] getTracksRemoving(int[] tracks, int removedTrack) {
        int[] newTracks = new int[tracks.length - 1];
        int trackCount = 0;
        for (int track : tracks) {
            if (track != removedTrack) {
                newTracks[trackCount++] = track;
            }
        }
        return newTracks;
    }

    // Internal classes.

    private class ComponentListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            CustomTrackSelectionView.this.onClick(view);
        }
    }

    private static final class TrackInfo {
        public final int groupIndex;
        public final int trackIndex;
        public final Format format;

        public TrackInfo(int groupIndex, int trackIndex, Format format) {
            this.groupIndex = groupIndex;
            this.trackIndex = trackIndex;
            this.format = format;
        }
    }

}