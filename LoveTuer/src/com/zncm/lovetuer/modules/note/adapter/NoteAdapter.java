package com.zncm.lovetuer.modules.note.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zncm.lovetuer.R;
import com.zncm.lovetuer.data.base.NoteData;

import java.util.List;

public abstract class NoteAdapter extends BaseAdapter {

    private List<NoteData> items;
    private Activity ctx;

    public NoteAdapter(Activity ctx) {
        this.ctx = ctx;
    }

    public void setItems(List<NoteData> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (items != null) {
            return items.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (items != null) {
            return position;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NoteViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(ctx).inflate(
                    R.layout.cell_lv_note, null);
            holder = new NoteViewHolder();
            holder.tvContent = (TextView) convertView
                    .findViewById(R.id.tvContent);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            holder.tvAuthor = (TextView) convertView.findViewById(R.id.tvAuthor);
            holder.tvReply = (TextView) convertView.findViewById(R.id.Reply);
            holder.tvReply = (TextView) convertView.findViewById(R.id.Reply);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            convertView.setTag(holder);
        } else {
            holder = (NoteViewHolder) convertView.getTag();
        }
        setData(position, holder);
        return convertView;
    }

    public abstract void setData(int position, NoteViewHolder holder);

    public class NoteViewHolder {
        public TextView tvContent;
        public TextView tvTime;
        public TextView tvTitle;
        public TextView tvAuthor;
        public TextView tvReply;
        public ImageView ivIcon;

    }
}