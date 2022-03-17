package com.example.vplayed_test.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.vplayed_test.R;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.util.Assertions;

import java.util.Collections;
import java.util.List;

public class TrackSelectionDialog extends DialogFragment {

    private DialogInterface.OnDismissListener onDismissListener;
    private final SparseArray<TrackSelectionViewFragment> tabFragments;
    private AppCompatDialog dialog;
    private static Integer tabName = 0;

    private static MappingTrackSelector.MappedTrackInfo mappedTrackInfo;

    /**
     * Creates a dialog for a given {@link DefaultTrackSelector}, whose parameters will be
     * automatically updated when tracks are selected.
     *
     * @param trackSelector     The {@link DefaultTrackSelector}.
     * @param onDismissListener A {@link DialogInterface.OnDismissListener} to call when the dialog is
     *                          dismissed.
     */
    public static TrackSelectionDialog createForTrackSelector(Integer title, DefaultTrackSelector trackSelector, DialogInterface.OnDismissListener onDismissListener) {
        tabName = title;
        mappedTrackInfo =
                Assertions.checkNotNull(trackSelector.getCurrentMappedTrackInfo());
        TrackSelectionDialog trackSelectionDialog = new TrackSelectionDialog();
        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
        trackSelectionDialog.init(
                mappedTrackInfo,
                /* initialParameters = */ parameters,
                /* onClickListener= */ (dialog, which) -> {
                    DefaultTrackSelector.ParametersBuilder builder = parameters.buildUpon();
                    builder.clearSelectionOverrides();
                    for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
                        builder
                                .clearSelectionOverrides(i)
                                .setRendererDisabled(i,
                                        trackSelectionDialog.getIsDisabled(/* rendererIndex= */ i));

                        List<DefaultTrackSelector.SelectionOverride> overrides =
                                trackSelectionDialog.getOverrides(/* rendererIndex= */ i);

                        if (!overrides.isEmpty()) {
                            builder.setSelectionOverride(
                                    /* rendererIndex= */ i,
                                    mappedTrackInfo.getTrackGroups(/* rendererIndex= */ i),
                                    overrides.get(0));
                        }
                    }
                    trackSelector.setParameters(builder);
                    trackSelectionDialog.onDismissListener.onDismiss(dialog);
                    trackSelectionDialog.dismiss();
                },
                onDismissListener);

        return trackSelectionDialog;
    }

    public TrackSelectionDialog() {
        tabFragments = new SparseArray<>();
        setRetainInstance(true);
    }

    private void init(
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo,
            DefaultTrackSelector.Parameters initialParameters,
            DialogInterface.OnClickListener onClickListener,
            DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
            TrackGroupArray trackGroupArray = mappedTrackInfo.getTrackGroups(i);
            TrackSelectionViewFragment tabFragment = new TrackSelectionViewFragment();
            tabFragment.init(
                    mappedTrackInfo,
                    /* rendererIndex= */ i,
                    initialParameters.getRendererDisabled(/* rendererIndex= */ i),
                    initialParameters.getSelectionOverride(/* rendererIndex= */ i, trackGroupArray),
                    onClickListener, dialog);
            tabFragments.put(i, tabFragment);
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // We need to own the view to let tab layout work correctly on all API levels. We can't use
        // AlertDialog because it owns the view itself, so we use AppCompatDialog instead, themed using
        // the AlertDialog theme overlay with force-enabled title.


        dialog =
                new AppCompatDialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setTitle(null);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();

//        window.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.MATCH_PARENT
//        );

        window.setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        onDismissListener.onDismiss(dialog);


    }


    /**
     * Returns whether a renderer is disabled.
     *
     * @param rendererIndex Renderer index.
     * @return Whether the renderer is disabled.
     */
    public boolean getIsDisabled(int rendererIndex) {
        TrackSelectionViewFragment rendererView = tabFragments.get(rendererIndex);
        return rendererView != null && rendererView.isDisabled;
    }

    /**
     * Returns the list of selected track selection overrides for the specified renderer. There will
     * be at most one override for each track group.
     *
     * @param rendererIndex Renderer index.
     * @return The list of track selection overrides for this renderer.
     */
    public List<DefaultTrackSelector.SelectionOverride> getOverrides(int rendererIndex) {
        TrackSelectionViewFragment rendererView = tabFragments.get(rendererIndex);
        return rendererView == null ? Collections.emptyList() : rendererView.overrides;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.trackselectiondialog, container, false);
//        ImageView closebutton = dialogView.findViewById(R.id.back);
        ConstraintLayout parentSection = dialogView.findViewById(R.id.parent_section);

        parentSection.setOnClickListener(v -> {
            onDismissListener.onDismiss(dialog);
            dialog.dismiss();
        });
//        closebutton.setOnClickListener(v -> {
//            onDismissListener.onDismiss(dialog);
//            dialog.dismiss();
//        });
        return dialogView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ConstraintLayout qualitySection = view.findViewById(R.id.ll_quality_section);


        if (tabName == 0) {
            qualitySection.setVisibility(View.VISIBLE);

        } else {
            qualitySection.setVisibility(View.GONE);

        }

        for (int i = 0; i < tabFragments.size(); i++) {
            if (tabName == 0) {
                if (i == 0) {
                    //Quality
                    updateTrackSelectionFragment(tabFragments.get(i), R.id.fl_quality);
                }
            }
        }

        if (mappedTrackInfo != null) {
            for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
                if (tabName == 0) {
                    if (i == 0 && mappedTrackInfo.getTrackGroups(i).isEmpty()) {
                        qualitySection.setVisibility(View.GONE);
                    }
                } else {
                    if (i == 2 && mappedTrackInfo.getTrackGroups(i).isEmpty()) {

                    } else if (i == 1 && mappedTrackInfo.getTrackGroups(i).isEmpty()) {

                    }
                }

            }
        }
    }

    private void updateTrackSelectionFragment(TrackSelectionViewFragment tabFragment, int frameLayout) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(frameLayout, tabFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    /**
     * Fragment to show a track selection in tab of the track selection dialog.
     */
    public static final class TrackSelectionViewFragment extends Fragment
            implements CustomTrackSelectionView.TrackSelectionListener {

        private MappingTrackSelector.MappedTrackInfo mappedTrackInfo;
        private int rendererIndex;
        private DialogInterface.OnClickListener onClickListener;
        private AppCompatDialog dialog;

        boolean isDisabled;
        List<DefaultTrackSelector.SelectionOverride> overrides;

        public TrackSelectionViewFragment() {
            // Retain instance across activity re-creation to prevent losing access to init data.
            setRetainInstance(true);
        }

        public void init(
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo,
                int rendererIndex,
                boolean initialIsDisabled,
                @Nullable DefaultTrackSelector.SelectionOverride initialOverride, DialogInterface.OnClickListener onClickListener, AppCompatDialog dialog) {
            this.mappedTrackInfo = mappedTrackInfo;
            this.rendererIndex = rendererIndex;
            this.isDisabled = initialIsDisabled;
            this.onClickListener = onClickListener;
            this.dialog = dialog;
            this.overrides =
                    initialOverride == null
                            ? Collections.emptyList()
                            : Collections.singletonList(initialOverride);
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getActivity() != null && getActivity().getWindow() != null) {
                getActivity().getWindow().setNavigationBarColor(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.black
                        )
                );
            }
        }

        @Override
        public View onCreateView(
                LayoutInflater inflater,
                @Nullable ViewGroup container,
                @Nullable Bundle savedInstanceState) {
            View rootView =
                    inflater.inflate(
                            R.layout.exo_track_selection_dialog, container, /* attachToRoot= */ false);
            CustomTrackSelectionView trackSelectionView = rootView.findViewById(R.id.exo_track_selection_view);

            if (rendererIndex == 2) {
                trackSelectionView.setShowDisableOption(true);
            }

            //trackSelectionView.setShowDefaultDisableOption(rendererIndex == 0);
            //is for more than one tracks
            trackSelectionView.setAllowAdaptiveSelections(true);

            trackSelectionView.setAllowMultipleOverrides(false);
            trackSelectionView.init(
                    mappedTrackInfo,
                    rendererIndex,
                    isDisabled,
                    overrides,
                    /* trackFormatComparator= */ null,
                    /* listener= */ this);
            return rootView;
        }

        @Override
        public void onTrackSelectionChanged(
                boolean isDisabled, @NonNull List<DefaultTrackSelector.SelectionOverride> overrides) {
            this.isDisabled = isDisabled;
            this.overrides = overrides;
            onClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
        }
    }
}
