package com.nk2sp.ifra;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class Test_AlarmAdapter extends RecyclerView.Adapter<Test_AlarmAdapter.BoardViewHolder>{
    private List<Test_Alarm> dataList; //데이터 리스트
    private Context resources;
    public Test_AlarmAdapter(List<Test_Alarm> dataList){ this.dataList = dataList;}

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //ViewHolder 객체 생성 후 리턴.
        return new BoardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_9_test_nf_warning, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
        // ViewHolder 가 재활용 될 때 사용되는 메소드
        Test_Alarm data = dataList.get(position);
        holder.filename.setText(data.getFilename());
        holder.filedate.setText(data.getFiledate());
        holder.Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
                View dialogView = inflater.inflate(R.layout.activity_9_test_nf_picture, null);

                ImageView imageView = dialogView.findViewById(R.id.im_NF); // 이미지뷰
                Random rand = new Random();
                int num = rand.nextInt(3);
                if (num == 0){imageView.setImageResource(R.drawable.op1);}
                else if (num == 1) {imageView.setImageResource(R.drawable.op2);}
                else{imageView.setImageResource(R.drawable.op3);}
                builder.setView(dialogView)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
    @Override
    public int getItemCount() { return dataList.size();} // 전체 데이터의 개수 조회

    public Context getResources() {
        return resources;
    }

    public class BoardViewHolder extends RecyclerView.ViewHolder {
        private TextView filename; //ViewHolder 에 필요한 데이터들
        private TextView filedate;
        private ImageButton Picture;
        public BoardViewHolder(@NonNull View itemView) {
            super(itemView);
            filename = itemView.findViewById(R.id.tv_mm_fnR);
            filedate = itemView.findViewById(R.id.tv_mm_dateR);
            Picture = itemView.findViewById(R.id.ibtn_Pic);
        }
    }
}

