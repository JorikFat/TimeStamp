package dev.jorik.timestamp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.jorik.timestamp.model.TimeStamp;


public class TimeStampAdapterRV extends RecyclerView.Adapter<TimeStampAdapterRV.TimestampVH> {

    private Context context;
    private List<TimeStamp> timestampData = new ArrayList<>();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private ClickListener cl;

    public interface ClickListener{
        void onClick(int id);
    }


    public TimeStampAdapterRV(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public TimestampVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_recyclerview_timestamp, parent, false);
        return new TimestampVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimestampVH holder, int position) {
        TimeStamp timeStamp = timestampData.get(position);
        String tsDesc = (timeStamp.getName() == null) ? "" : timeStamp.getName();
        holder.textTime.setText(DATE_FORMAT.format(timeStamp.getTime()));
        holder.textDescription.setText(tsDesc);
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return timestampData.size();
    }

    public void setData(List<TimeStamp> data) {
        timestampData.clear();
        timestampData.addAll(data);
        notifyDataSetChanged();
    }

    public void add(TimeStamp timestamp){
        timestampData.add(timestamp);
        notifyDataSetChanged();
    }

    public void insertItem(TimeStamp timeStamp) {
        timestampData.add(timeStamp);
        Collections.sort(timestampData);
        notifyDataSetChanged();
    }

    //todo временный метод, для работы без mvp
    public List<TimeStamp> getElements(){
        return timestampData;
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.cl = clickListener;
    }

    public class TimestampVH extends RecyclerView.ViewHolder {

        private View view;
        private TextView textTime;
        private TextView textDescription;
        private int position;

        public TimestampVH(View itemView) {
            super(itemView);
            this.view = itemView;
            this.textTime = this.view.findViewById(R.id.tv_itemRV_time);
            this.textDescription = this.view.findViewById(R.id.tv_itemRV_name);
            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cl != null){
                        cl.onClick(position);
                    }
                }
            });
        }
    }
}
