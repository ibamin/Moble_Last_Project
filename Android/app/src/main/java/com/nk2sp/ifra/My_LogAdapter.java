package com.nk2sp.ifra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class My_LogAdapter extends RecyclerView.Adapter<My_LogAdapter.BoardViewHolder> {

    private List<My_Log> datalist;
    private Context resources;

    public My_LogAdapter(List<My_Log> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //ViewHolder 객체 생성 후 리턴.
        return new My_LogAdapter.BoardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_7_my_log_custom, parent, false));
    }

    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
        My_Log data = datalist.get(position);
        holder.in_time.setText(data.get_In_time());
        holder.out_time.setText(data.get_Out_time());
        //holder.state.setText(data.get_State());
    }
    public int getItemCount() { return datalist.size();} // 전체 데이터의 개수 조회
    public class BoardViewHolder extends RecyclerView.ViewHolder {
        private TextView in_time;
        private TextView out_time;
        private TextView state;

        public BoardViewHolder(@NonNull View itemView) {
            super(itemView);
            in_time = itemView.findViewById(R.id.tv_INTR);
            out_time = itemView.findViewById(R.id.tv_OUTTR);
        }
    }
}

