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

public class ContatoAdapter extends RecyclerView.Adapter<ContatoAdapter.MyViewholder> {

    private List<Usuario> contatos;
    private Context context;

    public ContatoAdapter(List<Usuario> listaContato,Context c) {
         this.contatos = listaContato;
         this.context = c;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos,parent,false);

        return new MyViewholder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int position) {

        Usuario usuario = contatos.get(position);
        holder.nome.setText(usuario.getNome());
        holder.email.setText(usuario.getEmail());
        //caso tenha uma imagem armazenada no firebase, ela sera exibida, caso contrario sera exibido uma imagem padrao
        if(usuario.getFoto() != null){
            Uri uri = Uri.parse(usuario.getFoto());
            Glide.with(context).load(uri).into(holder.foto);
        }else{
            holder.foto.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return contatos.size();
    }

    public class MyViewholder extends RecyclerView.ViewHolder {

        CircleImageView foto;
        TextView nome,email;


        public MyViewholder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageViewFotoContato);
            nome = itemView.findViewById(R.id.textViewNomeContato);
            email = itemView.findViewById(R.id.textViewEmailContato);
        }
    }

}
