package de.slg.messenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import de.slg.leoapp.R;
import de.slg.leoapp.User;

public class UserAdapter extends ArrayAdapter<User> {

    private Context context;
    private int resId;
    private User[] users;
    private boolean selectable;
    private View[] views;

    public UserAdapter(Context context, User[] users, boolean selectable) {
        super(context, R.layout.list_item_user, users);
        this.context = context;
        this.resId = R.layout.list_item_user;
        if (selectable)
            this.resId = R.layout.list_item_user_selectable;
        this.users = users;
        this.selectable = selectable;
        this.views = new View[users.length];
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        if (position < users.length && users[position] != null) {
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(resId, null);
            }
            final TextView username = (TextView) v.findViewById(R.id.username);
            username.setText(users[position].userName);
            views[position] = v;
        }
        return v;
    }

    public int selectCount() {
        if (selectable) {
            int count = 0;
            for (int i = 0; i < users.length; i++) {
                if (((CheckBox) views[i].findViewById(R.id.checkBox)).isChecked())
                    count++;
            }
            return count;
        }
        return -1;
    }

    public User[] getSelected() {
        if (selectable) {
            User[] result = new User[selectCount()];
            int i1 = 0;
            for (int i = 0; i < result.length; i++, i1++) {
                while (i1 < views.length && !((CheckBox) views[i1].findViewById(R.id.checkBox)).isChecked())
                    i1++;
                if (i1 < views.length)
                    result[i] = users[i1];
            }
            return result;
        }
        return null;
    }

    public void setUsers(User[] users) {
        this.users = users;
        notifyDataSetChanged();
    }
}