package com.client.mpanjifa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mpanjifa.R;
import com.client.mpanjifa.models.Client;

import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder>{
    public interface OnClientClickListener{
    void OnClientClick(Client client);
    void OnClientLongClick(Client client, View view);
    }

    private List<Client> clientList;
    private final OnClientClickListener listener;

    public ClientAdapter(List<Client> clientList,OnClientClickListener listener){
        this.clientList = clientList;
        this.listener = listener;
    }
    public void updateData(List<Client> newList){
        this.clientList = newList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_client_card,parent,false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder,int position){
        Client client = clientList.get(position);
        holder.bind(client,listener);
    }

    @Override
    public int getItemCount(){
        return clientList != null ? clientList.size() : 0;
    }

    static class ClientViewHolder extends  RecyclerView.ViewHolder {
        private final TextView tvId;
        private final TextView tvName;
        private final ImageView ivPhoto;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvClientId);
            tvName = itemView.findViewById(R.id.tvClientName);
            ivPhoto = itemView.findViewById(R.id.ivClientPhoto);
        }

        public void bind(final Client client, final OnClientClickListener listener) {
            tvId.setText("Id : " + client.getIdcli());
            tvName.setText(client.getNom());

            Glide.with(itemView.getContext())
                    .load(client.getPhoto())
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivPhoto);

            itemView.setOnClickListener(v -> listener.OnClientClick(client));

            itemView.setOnLongClickListener(v -> {
                listener.OnClientLongClick(client, v);
                return true;
            });
        }
    }
}