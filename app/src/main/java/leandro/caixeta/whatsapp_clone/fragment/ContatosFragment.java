package leandro.caixeta.whatsapp_clone.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import leandro.caixeta.whatsapp_clone.R;
import leandro.caixeta.whatsapp_clone.activity.ChatActivity;
import leandro.caixeta.whatsapp_clone.adapter.ContatoAdapter;
import leandro.caixeta.whatsapp_clone.config.ConfiguracaoFirebase;
import leandro.caixeta.whatsapp_clone.helper.RecyclerItemClickListener;
import leandro.caixeta.whatsapp_clone.helper.UsuarioFirebase;
import leandro.caixeta.whatsapp_clone.model.Usuario;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContatosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContatosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recycleViewListarContatos;
    private ContatoAdapter contatoAdapter;
    private ArrayList<Usuario> listaContato = new ArrayList<>();
    private DatabaseReference usuarioRef ;
    private ValueEventListener eventListenerContato;
    private FirebaseUser userAtual ;

    public ContatosFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ContatosFragment newInstance(String param1, String param2) {
        ContatosFragment fragment = new ContatosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //configuraçoes iniciais
        recycleViewListarContatos = view.findViewById(R.id.recycleViewListarContatos);
        usuarioRef = ConfiguracaoFirebase.getDatase().child("usuarios");
        userAtual = UsuarioFirebase.getUsuarioAtual();

        //Configurando adapter
        contatoAdapter = new ContatoAdapter(listaContato,getActivity());

        //Configurar recycleView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycleViewListarContatos.setLayoutManager(layoutManager);
        recycleViewListarContatos.setHasFixedSize( true);
        recycleViewListarContatos.setAdapter(contatoAdapter);

        //Configurando evento de clique no recyclerview
        recycleViewListarContatos.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(), recycleViewListarContatos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Usuario usuario = listaContato.get(position);
                Intent i = new Intent(getActivity(), ChatActivity.class);
                i.putExtra("chatContato",usuario);
                startActivity(i);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }
        ));



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContato();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(eventListenerContato);
    }

    public void recuperarContato(){
        listaContato.clear();
       eventListenerContato = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dados: snapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);

                    String emailUsuairoAtual = userAtual.getEmail();
                    if(!emailUsuairoAtual.equals(usuario.getEmail())){
                        listaContato.add(usuario);
                    }

                }

                contatoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}