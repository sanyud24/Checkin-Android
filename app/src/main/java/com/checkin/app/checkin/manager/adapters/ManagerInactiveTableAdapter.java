package com.checkin.app.checkin.manager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.session.models.RestaurantTableModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagerInactiveTableAdapter extends RecyclerView.Adapter<ManagerInactiveTableAdapter.ViewHolder> {
    private List<RestaurantTableModel> mList;
    private ManagerTableInitiate mListener;

    public ManagerInactiveTableAdapter(ManagerTableInitiate listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_manager_work_initiate_table;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setData(List<RestaurantTableModel> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public interface ManagerTableInitiate {
        void onClickInactiveTable(RestaurantTableModel tableModel);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_mw_table_number)
        TextView tvManagerTableNumber;
        private RestaurantTableModel mTableModel;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> mListener.onClickInactiveTable(mTableModel));
        }

        public void bindData(RestaurantTableModel data) {
            this.mTableModel = data;
            String[] split = data.getTable().split(" ");
            tvManagerTableNumber.setText(split.length > 1 ? split[1] : split[0]);
        }
    }
}
