package de.slg.startseite;

interface RecyclerViewItemListener {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
