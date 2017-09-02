package de.slg.startseite;

public interface RecyclerViewItemListener {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

}
