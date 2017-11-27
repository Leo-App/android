package de.slg.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;

import static de.slg.leoapp.sqlite.SQLiteConnectorMessenger.DBHelper.USER_DEFAULTNAME;
import static de.slg.leoapp.sqlite.SQLiteConnectorMessenger.DBHelper.USER_NAME;
import static de.slg.leoapp.sqlite.SQLiteConnectorMessenger.DBHelper.USER_STUFE;

public class SearchFragment extends Fragment {
    public View         view;
    public RecyclerView rvSearch;
    boolean initialized = false;
    private Object[]             data;
    private View.OnClickListener clickListener;

    private boolean expanded;

    private String  suchbegriff = "";
    private boolean chatsFirst  = false;
    private String  name        = USER_DEFAULTNAME;
    private boolean nameDesc    = false, groupGrade = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_search, container, false);
            data = de.slg.leoapp.utility.Utils.getController().getMessengerDatabase().getSuchergebnisse(suchbegriff, chatsFirst, USER_STUFE + ", " + name);
            initRecyclerView();
            initSearch();
            initSort();
        }
        initialized = true;
        return view;
    }

    private void initRecyclerView() {
        rvSearch = (RecyclerView) view.findViewById(R.id.recyclerViewSearch);
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = rvSearch.getChildAdapterPosition(v);
                if (data[position] instanceof User) {
                    User clickedUser = (User) data[position];
                    startActivity(new Intent(getContext(), ChatActivity.class)
                            .putExtra("uid", clickedUser.uid)
                            .putExtra("cid", Utils.getController().getMessengerDatabase().getChatWith(clickedUser.uid))
                            .putExtra("cname", clickedUser.uname)
                            .putExtra("ctype", Chat.ChatType.PRIVATE.toString()));
                } else {
                    Chat clickedChat = (Chat) data[position];
                    startActivity(new Intent(getContext(), ChatActivity.class)
                            .putExtra("cid", clickedChat.cid)
                            .putExtra("cname", clickedChat.cname)
                            .putExtra("ctype", Chat.ChatType.GROUP.toString()));
                }
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                rvSearch.getContext(),
                linearLayoutManager.getOrientation()
        );
        rvSearch.addItemDecoration(mDividerItemDecoration);
        rvSearch.setLayoutManager(linearLayoutManager);

        rvSearch.setAdapter(new HybridAdapter(getActivity().getLayoutInflater()));
    }

    private void initSearch() {
        TextView input = (TextView) view.findViewById(R.id.editText);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                suchbegriff = s.toString();
                refreshUI();
            }
        });
    }

    private void initSort() {
        expanded = false;
        view.findViewById(R.id.sortCard).setVisibility(View.GONE);
        final FloatingActionButton expand = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expanded = !expanded;
                if (expanded) {
                    expand.setImageResource(R.drawable.ic_expand_less_white_24dp);
                    SearchFragment.this.view.findViewById(R.id.sortCard).setVisibility(View.VISIBLE);
                } else {
                    expand.setImageResource(R.drawable.ic_expand_more_white_24dp);
                    SearchFragment.this.view.findViewById(R.id.sortCard).setVisibility(View.GONE);
                }
            }
        });
        final Button first = (Button) view.findViewById(R.id.buttonFirst);
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatsFirst) {
                    first.setText("Benutzer");
                    chatsFirst = false;
                } else {
                    first.setText("Chats");
                    chatsFirst = true;
                }
                refreshUI();
            }
        });
        if (!chatsFirst) {
            first.setText("Benutzer");
        } else {
            first.setText("Chats");
        }
        final Button sortName = (Button) view.findViewById(R.id.buttonName);
        sortName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.equals(USER_DEFAULTNAME)) {
                    name = USER_NAME;
                    sortName.setText("Nickname");
                } else {
                    name = USER_DEFAULTNAME;
                    sortName.setText("Standardname");
                }
                refreshUI();
            }
        });
        if (!name.equals(USER_DEFAULTNAME)) {
            sortName.setText("Nickname");
        } else {
            sortName.setText("Standardname");
        }
        final ImageButton nameUpDown = (ImageButton) view.findViewById(R.id.buttonNameUpDown);
        nameUpDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameDesc) {
                    nameDesc = false;
                    nameUpDown.setImageResource(R.drawable.ic_expand_less_white_24dp);
                } else {
                    nameDesc = true;
                    nameUpDown.setImageResource(R.drawable.ic_expand_more_white_24dp);
                }
                refreshUI();
            }
        });
        if (!nameDesc) {
            nameUpDown.setImageResource(R.drawable.ic_expand_less_white_24dp);
        } else {
            nameUpDown.setImageResource(R.drawable.ic_expand_more_white_24dp);
        }
        final Button grade = (Button) view.findViewById(R.id.buttonGrade);
        grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grade.setActivated(groupGrade);
                if (groupGrade)
                    grade.setTextColor(ContextCompat.getColor(getContext(), R.color.colorInactive));
                else
                    grade.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                groupGrade = !groupGrade;
                refreshUI();
            }
        });
        grade.setActivated(!groupGrade);
        if (!groupGrade)
            grade.setTextColor(ContextCompat.getColor(getContext(), R.color.colorInactive));
        else
            grade.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
    }

    public void refreshUI() {
        if (getActivity() != null && initialized) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (rvSearch != null) {
                        String orderUser = "";
                        if (groupGrade)
                            orderUser = USER_STUFE + ", ";
                        orderUser += name;
                        if (nameDesc)
                            orderUser += " DESC";
                        data = Utils.getController().getMessengerDatabase().getSuchergebnisse(suchbegriff, chatsFirst, orderUser);
                        rvSearch.swapAdapter(new HybridAdapter(getActivity().getLayoutInflater()), false);
                    }
                }
            });
        }
    }

    private class HybridAdapter extends RecyclerView.Adapter {
        private final LayoutInflater inflater;

        HybridAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new HybridAdapter.ViewHolder(inflater.inflate(R.layout.list_item_user, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            View            v           = holder.itemView;
            Object          current     = data[position];
            final TextView  username    = (TextView) v.findViewById(R.id.username);
            final TextView  userdefault = (TextView) v.findViewById(R.id.userdefault);
            final ImageView icon        = (ImageView) v.findViewById(R.id.iconUser);
            if (current instanceof User) {
                User u = (User) current;
                username.setText(u.uname);
                userdefault.setText(u.udefaultname + ", " + u.ustufe);
                icon.setImageResource(R.drawable.ic_account_circle_black_24dp);
            } else {
                Chat c = (Chat) current;
                username.setText(c.cname);
                userdefault.setText("");
                icon.setImageResource(R.drawable.ic_question_answer_white_24dp);
            }
        }

        @Override
        public int getItemCount() {
            return data.length;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(clickListener);
                itemView.findViewById(R.id.checkBox).setVisibility(View.INVISIBLE);
            }
        }
    }
}
