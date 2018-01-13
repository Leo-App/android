package de.slg.messenger.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;
import de.slg.messenger.activity.ChatActivity;
import de.slg.messenger.utility.Chat;

public class ChatsFragment extends Fragment {
    public  RecyclerView             rvChats;
    private Chat[]                   chatArray;
    private View                     view;
    private View.OnClickListener     chatClickListener;
    private View.OnLongClickListener chatLongClickListener;
    private int                      selected;
    private int                      previousPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
            initRecyclerView();
        }
        return view;
    }

    private void initRecyclerView() {
        selected = -1;
        rvChats = (RecyclerView) view.findViewById(R.id.recyclerView);

        chatClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int  position    = rvChats.getChildAdapterPosition(view);
                Chat clickedChat = chatArray[position];
                startActivity(new Intent(getContext(), ChatActivity.class)
                        .putExtra("cid", clickedChat.cid)
                        .putExtra("cname", clickedChat.cname)
                        .putExtra("ctype", clickedChat.ctype.toString()));
                view.findViewById(R.id.notify).setVisibility(View.GONE);
                view.findViewById(R.id.imageButtonDelete).setVisibility(View.GONE);
                view.findViewById(R.id.imageButtonMute).setVisibility(View.GONE);
            }
        };

        chatLongClickListener = new View.OnLongClickListener() {
            private int visibility;

            @Override
            public boolean onLongClick(final View view) {
                if (previousPosition != -1) {
                    rvChats.getChildAt(previousPosition).findViewById(R.id.imageButtonDelete).setVisibility(View.GONE);
                    rvChats.getChildAt(previousPosition).findViewById(R.id.imageButtonMute).setVisibility(View.GONE);
                    rvChats.getChildAt(previousPosition).findViewById(R.id.notify).setVisibility(visibility);
                }
                previousPosition = rvChats.getChildAdapterPosition(view);
                final View delete = view.findViewById(R.id.imageButtonDelete);
                final View mute   = view.findViewById(R.id.imageButtonMute);
                final View notify = view.findViewById(R.id.notify);
                visibility = notify.getVisibility();
                notify.setVisibility(View.GONE);
                delete.setVisibility(View.VISIBLE);
                mute.setVisibility(View.VISIBLE);
                return true;
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                rvChats.getContext(),
                linearLayoutManager.getOrientation()
        );
        rvChats.addItemDecoration(mDividerItemDecoration);
        rvChats.setLayoutManager(linearLayoutManager);

        chatArray = Utils.getController().getMessengerDatabase().getChats();
        rvChats.setAdapter(new ChatAdapter(getActivity().getLayoutInflater(), chatArray, chatClickListener, chatLongClickListener));
    }

    public void refreshUI() {
        chatArray = Utils.getController().getMessengerDatabase().getChats();
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (rvChats != null)
                        rvChats.swapAdapter(new ChatAdapter(getActivity().getLayoutInflater(), chatArray, chatClickListener, chatLongClickListener), false);
                }
            });
        }
    }

    private class ChatAdapter extends RecyclerView.Adapter {
        private final LayoutInflater           inflater;
        private final Chat[]                   chats;
        private final View.OnClickListener     clickListener;
        private final View.OnLongClickListener longClickListener;

        ChatAdapter(LayoutInflater inflater, Chat[] chats, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
            this.inflater = inflater;
            this.chats = chats;
            this.clickListener = clickListener;
            this.longClickListener = longClickListener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ChatAdapter.ViewHolder(inflater.inflate(R.layout.list_item_chat, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final View      v            = holder.itemView;
            final TextView  chatname     = (TextView) v.findViewById(R.id.chatname);
            final TextView  lastMessage  = (TextView) v.findViewById(R.id.letzteNachricht);
            final ImageView icon         = (ImageView) v.findViewById(R.id.iconChat);
            final View      iconMute     = v.findViewById(R.id.iconMute);
            final View      buttonDelete = v.findViewById(R.id.imageButtonDelete);
            final View      buttonMute   = v.findViewById(R.id.imageButtonMute);
            final View      notify       = v.findViewById(R.id.notify);
            final Chat      c            = chats[position];
            if (c != null) {
                chatname.setText(c.cname);
                if (c.m != null) {
                    lastMessage.setVisibility(View.VISIBLE);
                    lastMessage.setText(c.m.uname + ": " + c.m.mtext);
                    if (!c.m.mread)
                        notify.setVisibility(View.VISIBLE);
                    else
                        notify.setVisibility(View.GONE);
                } else {
                    lastMessage.setVisibility(View.GONE);
                    notify.setVisibility(View.GONE);
                }
                if (c.ctype == Chat.ChatType.PRIVATE) {
                    icon.setImageResource(R.drawable.ic_chat_bubble_white_24dp);
                } else {
                    icon.setImageResource(R.drawable.icon_messenger);
                }
                if (c.cmute) {
                    iconMute.setVisibility(View.VISIBLE);
                } else {
                    iconMute.setVisibility(View.GONE);
                }
                buttonMute.setActivated(c.cmute);
                if (position != selected) {
                    buttonDelete.setVisibility(View.GONE);
                    buttonMute.setVisibility(View.GONE);
                } else {
                    buttonDelete.setVisibility(View.VISIBLE);
                    buttonMute.setVisibility(View.VISIBLE);
                }
                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.getController().getMessengerDatabase().deleteChat(c.cid);
                        selected = -1;
                        previousPosition = -1;
                        Utils.getController().getMessengerActivity().notifyUpdate();
                    }
                });
                buttonMute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.getController().getMessengerDatabase().muteChat(c.cid, !c.cmute);
                        selected = -1;
                        Utils.getController().getMessengerActivity().notifyUpdate();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return chats.length;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(clickListener);
                itemView.setOnLongClickListener(longClickListener);
            }
        }
    }
}
