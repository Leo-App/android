package de.slg.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;

public class UserFragment extends Fragment {
    public View         view;
    public RecyclerView rvUsers;
    View.OnClickListener userClickListener;
    private User[] userArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
            initRecyclerView();
        }
        return view;
    }

    private void initRecyclerView() {
        rvUsers = (RecyclerView) view.findViewById(R.id.recyclerView);
        userClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int  position    = rvUsers.getChildAdapterPosition(view);
                User clickedUser = userArray[position];
                startActivity(new Intent(getContext(), ChatActivity.class)
                        .putExtra("uid", clickedUser.uid)
                        .putExtra("cid", Utils.getController().getMessengerDatabase().getChatWith(clickedUser.uid))
                        .putExtra("cname", clickedUser.uname)
                        .putExtra("ctype", Chat.ChatType.PRIVATE.toString()));
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                rvUsers.getContext(),
                linearLayoutManager.getOrientation()
        );
        rvUsers.addItemDecoration(mDividerItemDecoration);
        rvUsers.setLayoutManager(linearLayoutManager);

        userArray = Utils.getController().getMessengerDatabase().getUsers();
        rvUsers.setAdapter(new UserAdapter(getActivity().getLayoutInflater(), userArray, userClickListener));
    }

    public void refreshUI() {
        userArray = Utils.getController().getMessengerDatabase().getUsers();
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (rvUsers != null)
                        rvUsers.swapAdapter(new UserAdapter(getActivity().getLayoutInflater(), userArray, userClickListener), false);
                }
            });
        }
    }

    private class UserAdapter extends RecyclerView.Adapter {
        private final LayoutInflater       inflater;
        private final User[]               array;
        private final View.OnClickListener listener;

        UserAdapter(LayoutInflater inflater, User[] array, View.OnClickListener listener) {
            this.inflater = inflater;
            this.array = array;
            this.listener = listener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UserAdapter.ViewHolder(inflater.inflate(R.layout.list_item_user, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            View           v           = holder.itemView;
            final TextView username    = (TextView) v.findViewById(R.id.username);
            final TextView userdefault = (TextView) v.findViewById(R.id.userdefault);
            username.setText(array[position].uname);
            userdefault.setText(array[position].udefaultname + ", " + array[position].ustufe);
            v.findViewById(R.id.checkBox).setVisibility(View.INVISIBLE);
        }

        @Override
        public int getItemCount() {
            return array.length;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(listener);
            }
        }
    }
}
