package com.smallangrycoders.nevermorepayforwater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StCityAdapter extends RecyclerView.Adapter<StCityAdapter.ViewHolder> {

    interface OnStCityClickListener {
        void onStCityClick(StCity state, int position);
    }

    private OnStCityClickListener onClickListener;
    DateTimeFormatter formatq = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final LayoutInflater inflater;
    private List<StCity> states;
    private final Context context;

    StCityAdapter(Context context, List<StCity> states, OnStCityClickListener onClickListener, Context context1) {
        this.onClickListener = onClickListener;
        this.states = states;
        this.inflater = LayoutInflater.from(context);
        this.context = context1;
    }

    public void SetOnCl(OnStCityClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    public void setArrayMyData(List<StCity> arrayMyData) {
        this.states = arrayMyData;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StCity state = states.get(position);
        holder.nameView.setText(state.getName());

        String tempro = this.context.getString(R.string.tempro);
        holder.tempView.setText(tempro + state.getTemp());

        if (state.getSyncDate() != null) {
            String refreshed = this.context.getString(R.string.refreshed);
            holder.syncDateView.setText(refreshed + state.getSyncDate().format(formatq));
        }

        // Обработчик клика на весь элемент (существующий)
        holder.itemView.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onStCityClick(state, holder.getAdapterPosition());
            }
        });

        // 👇 ИСПРАВЛЕННЫЙ обработчик для кнопки отопления
        if (holder.btnHeating != null) {
            holder.btnHeating.setOnClickListener(v -> {
                Intent intent = new Intent(context, HeatingInputActivity.class);
                intent.putExtra("city_id", state.getId()); // нужно добавить getId() в StCity
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return states.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView, tempView, syncDateView;
        final Button btnHeating;  // 👈 ДОБАВИТЬ ЭТУ СТРОКУ

        ViewHolder(View view) {
            super(view);
            nameView = view.findViewById(R.id.name);
            tempView = view.findViewById(R.id.temp);
            syncDateView = view.findViewById(R.id.syncDate);
            btnHeating = view.findViewById(R.id.btnHeating);  // 👈 ДОБАВИТЬ ЭТУ СТРОКУ
        }
    }
}