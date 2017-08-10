package de.slg.messenger;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import de.slg.leoapp.R;
import de.slg.leoapp.User;

class UserAdapter extends ArrayAdapter<User> {
    private final int resId;
    private final User[] users;
    private final LayoutInflater inflater;
    private final boolean selectable;
    private final View[] views;

    UserAdapter(Context context, User[] users, boolean selectable) {
        super(context, R.layout.list_item_user, users);
        this.resId = R.layout.list_item_user;
        this.users = users;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.selectable = selectable;
        this.views = new View[users.length];
    }

    @NonNull
    @Override
    public View getView(int position, View v, @NonNull ViewGroup parent) {
        if (users[position] != null) {
            if (v == null) {
                v = inflater.inflate(resId, null);

                final TextView username = (TextView) v.findViewById(R.id.username);
                final TextView userdefault = (TextView) v.findViewById(R.id.userdefault);
                username.setText(users[position].uname);
                userdefault.setText(users[position].udefaultname + ", " + users[position].ustufe);
                if (selectable)
                    v.findViewById(R.id.checkBox).setVisibility(View.VISIBLE);
                else
                    v.findViewById(R.id.checkBox).setVisibility(View.INVISIBLE);
                views[position] = v;
            }
        }
        return v;
    }

    int selectCount() {
        if (selectable) {
            int count = 0;
            for (int i = 0; i < users.length; i++) {
                if (views[i] != null && ((CheckBox) views[i].findViewById(R.id.checkBox)).isChecked())
                    count++;
            }
            return count;
        }
        return -1;
    }

    User[] getSelected() {
        if (selectable) {
            User[] result = new User[selectCount()];
            int i1 = 0;
            for (int i = 0; i < result.length; i++, i1++) {
                while (i1 < views.length && (views[i1] == null || !((CheckBox) views[i1].findViewById(R.id.checkBox)).isChecked()))
                    i1++;
                if (i1 < views.length)
                    result[i] = users[i1];
            }
            return result;
        }
        return null;
    }
}