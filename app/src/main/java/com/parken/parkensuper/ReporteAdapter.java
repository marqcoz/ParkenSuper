package com.parken.parkensuper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;


public class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.ViewHolder> {

        private Context context;
        private List<Reporte> reporte;
        private Cursor items;
        private final OnItemClickListener listener;



        public ReporteAdapter(Context context, List<Reporte> reporte, OnItemClickListener listener) {
            this.context = context;
            this.reporte = reporte;
            this.listener = listener;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_reportes,parent,false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.bind(reporte.get(position), listener);

            Glide.with(context).load(reporte.get(position).getImgMapLink()).into(holder.imageMap);

            holder.tipo.setText(reporte.get(position).getTipoReporte());
            holder.espacioParken.setText(String.valueOf(reporte.get(position).getIdEspacioParken()));
            String time = reporte.get(position).getTiempoReporte() + " hrs";
            holder.tiempo.setText(time);
            holder.direccion.setText(reporte.get(position).getDireccionEspacioParken());
            holder.auto.setText(reporte.get(position).getNombreAutomovilista() + " " + reporte.get(position).getApellidoAutomovilista());


            String est = reporte.get(position).getEstatusReporte();
            holder.estatus.setText(est);
            //holder.statusIcon.setVisibility(View.INVISIBLE);
            holder.line.setVisibility(View.VISIBLE);


            if(est.equals("RESUELTO")){
                holder.estatus.setTextColor(Color.argb(255,46,204,113));
                holder.statusIcon.setImageResource(R.drawable.ic_check);
                holder.pago.setVisibility(View.GONE);
                holder.line.setVisibility(View.GONE);
            }
            if (est.equals("PENDIENTE")){
                holder.statusIcon.setImageResource(R.drawable.ic_alert);
                holder.estatus.setTextColor(Color.argb(255,52,73,94));
                holder.pago.setVisibility(View.VISIBLE);
                holder.line.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return reporte.size();
        }


        interface OnItemClickListener {
            void onItemClick(Reporte item);
        }

        public  class ViewHolder extends  RecyclerView.ViewHolder{
        //implements View.OnClickListener{

            public ImageView imageMap;
            public  ImageView statusIcon;
            public TextView espacioParken;
            public TextView auto;
            public TextView tipo;
            public ConstraintLayout pago;
            public TextView direccion;
            public TextView monto;
            public TextView estatus;
            public TextView tiempo;
            public Button action;

            public View line;



            public ViewHolder(View itemView) {
                super(itemView);

                imageMap = (ImageView) itemView.findViewById(R.id.map);
                statusIcon = (ImageView) itemView.findViewById(R.id.imageViewStatus);
                espacioParken = (TextView) itemView.findViewById(R.id.espacioParken);
                auto = (TextView) itemView.findViewById(R.id.automovilista);
                tiempo = (TextView) itemView.findViewById(R.id.tiempoReporte);
                //direccion = (TextView) itemView.findViewById(R.id.direccionEP);
                tipo = (TextView) itemView.findViewById(R.id.tipoReporte);
                direccion = (TextView) itemView.findViewById(R.id.direccion);
                estatus = (TextView) itemView.findViewById(R.id.estatusReporte);

                pago = itemView.findViewById(R.id.linearLayoutPagar);
                action = (Button)itemView.findViewById(R.id.buttonAction);

                line = itemView.findViewById(R.id.viewLine);

                //action.setOnClickListener(this);
                //itemView.setOnClickListener(this);
            }

            public void bind(final Reporte item, final OnItemClickListener listener) {

                action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(item);
                    }
                });
            }

                /*
            @Override
            public void onClick(View view) {
                if(view.getTag().toString().equals("action")){
                  //Click
                    Log.d("PressButton", "True");

                    //getAdapterPosition();
                }
                //escucha.onClick(this, obtenerIdAlquiler(getAdapterPosition()));
            }*/
        }

}
