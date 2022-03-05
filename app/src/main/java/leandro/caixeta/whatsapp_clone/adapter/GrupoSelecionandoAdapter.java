package leandro.caixeta.whatsapp_clone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import leandro.caixeta.whatsapp_clone.R;
import leandro.caixeta.whatsapp_clone.model.Usuario;

public class GrupoSelecionandoAdapter extends RecyclerView.Adapter<GrupoSelecionandoAdapter.MyViewholder>{

    private List<Usuario> contatosSelecionados;
    private Context context;

    public GrupoSelecionandoAdapter(List<Usuario> listaContato,Context c) {
        this.contatosSelecionados = listaContato;
        this.context = c;


    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_grupo_selecionado,parent,false);

        return new GrupoSelecionandoAdapter.MyViewholder(itemLista);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int position) {
        Usuario usuario = contatosSelecionados.get(position);
        boolean cabecalho = usuario.getEmail().isEmpty();


        holder.nome.setText(usuario.getNome());

        //caso tenha uma imagem armazenada no firebase, ela sera exibida, caso contrario sera exibido uma imagem padrao
        if(usuario.getFoto() != null){
            Uri uri = Uri.parse(usuario.getFoto());
            Glide.with(context).load(uri).into(holder.foto);
        }else{
            if(cabecalho){
                holder.foto.setImageResource(R.drawable.icone_grupo);
            }else {

                holder.foto.setImageResource(R.drawable.padrao);
            }
        }


    }

    @Override
    public int getItemCount() {
        return contatosSelecionados.size();
    }

    public class MyViewholder extends RecyclerView.ViewHolder {

        CircleImageView foto;
        TextView nome;

        public MyViewholder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imgGrupo);
            nome = itemView.findViewById(R.id.textViewNomeMembroSelecionado);
        }
    }



}
