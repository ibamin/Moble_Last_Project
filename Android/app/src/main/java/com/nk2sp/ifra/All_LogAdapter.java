package com.nk2sp.ifra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class All_LogAdapter extends RecyclerView.Adapter<All_LogAdapter.BoardViewHolder> {

    private List<All_Log> datalist;
    private Context resources;

    public All_LogAdapter(List<All_Log> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new All_LogAdapter.BoardViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_8_manage_page_alllog, parent, false));//ViewHolder 객체 생성 후 리턴.
    }

    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
        All_Log data = datalist.get(position);
        holder.inner_time.setText(data.getInner_time());
        holder.outter_time.setText(data.getOutter_time());
        //holder.state.setText(data.getState());
        holder.confidence.setText(data.getConfidence());
        holder.user_id.setText(data.getUser_id());
    }
    public int getItemCount() { return datalist.size();} // 전체 데이터의 개수 조회
    public class BoardViewHolder extends RecyclerView.ViewHolder {
        private TextView inner_time;
        private TextView outter_time;
        private TextView confidence;
        //private TextView state;
        private TextView user_id;

        public BoardViewHolder(@NonNull View itemView) {
            super(itemView);
            inner_time = itemView.findViewById(R.id.tv_AL_INR);
            outter_time = itemView.findViewById(R.id.tv_AL_OUTR);
            confidence = itemView.findViewById(R.id.tv_AL_confidenceR);
            //state = itemView.findViewById(R.id.tv_AL_);
            user_id = itemView.findViewById(R.id.tv_AL_nameR);
        }
    }
}
