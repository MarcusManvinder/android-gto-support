package org.ccci.gto.android.common.recyclerview.advrecyclerview.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;

public class ExpandableStateImageView extends AppCompatImageView {
    /**
     * State indicating the group is expanded.
     */
    private static final int[] GROUP_EXPANDED_STATE_SET = {android.R.attr.state_expanded};

    public ExpandableStateImageView(Context context) {
        super(context);
    }

    public ExpandableStateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableStateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int[] onCreateDrawableState(final int extraSpace) {
        final ExpandableItemViewHolder holder = findViewHolder(this);

        if (holder != null && (holder.getExpandStateFlags() & ExpandableItemConstants.STATE_FLAG_IS_EXPANDED) != 0) {
            return mergeDrawableStates(super.onCreateDrawableState(extraSpace + 1), GROUP_EXPANDED_STATE_SET);
        } else {
            return super.onCreateDrawableState(extraSpace);
        }
    }

    @Nullable
    private ExpandableItemViewHolder findViewHolder(@Nullable View view) {
        while (view != null) {
            final ViewParent parent = view.getParent();

            if (parent instanceof RecyclerView) {
                final RecyclerView.ViewHolder holder = ((RecyclerView) parent).getChildViewHolder(view);
                if (holder instanceof ExpandableItemViewHolder) {
                    return (ExpandableItemViewHolder) holder;
                }
            }

            view = parent instanceof View ? (View) parent : null;
        }

        return null;
    }
}
