package leandro.caixeta.whatsapp_clone.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import leandro.caixeta.whatsapp_clone.R;
import leandro.caixeta.whatsapp_clone.activity.ChatActivity;
import leandro.caixeta.whatsapp_clone.adapter.ContatoAdapter;
import leandro.caixeta.whatsapp_clone.adapter.ConversaAdapter;
import leandro.caixeta.whatsapp_clone.config.ConfiguracaoFirebase;
import leandro.caixeta.whatsapp_clone.helper.RecyclerItemClickListener;
import leandro.caixeta.whatsapp_clone.helper.UsuarioFirebase;
import leandro.caixeta.whatsapp_clone.model.Conversa;
import leandro.caixeta.whatsapp_clone.model.Usuario;


public class ConversasFragment extends Fragment {

    private RecyclerView recyclerListaConversa;
    private List<Conversa> conversas = new ArrayList<>();
    private ConversaAdapter adapter;
    private DatabaseReference databaseReference,conversaRef ;
    private ChildEventListener eventeConversa ;


    public ConversasFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversar();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversaRef.removeEventListener(eventeConversa);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        //Configuraçoes iniciais
        recyclerListaConversa = view.findViewById(R.id.recyclerListaConversa);
        databaseReference = ConfiguracaoFirebase.getDatase();

        //Configurar o adapter
        adapter = new ConversaAdapter(conversas,getActivity());

        //Configurar recycleView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerListaConversa.setLayoutManager(layoutManager);
        recyclerListaConversa.setHasFixedSize(true);
         recyclerListaConversa.setAdapter(adapter);

        //Configurar evento de click
        recyclerListaConversa.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerListaConversa, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Conversa conversa = conversas.get(position);

                //Verificando se a conversa é normal(de pessoa para pessoa) ou se é uma conversa em grupo
                if(conversa.getIsGrup().equals("true")){

                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatGrupo",conversa.getGrupo());
                    startActivity(i);
                }else{
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatContato",conversa.getUsuarioExibicao());
                    startActivity(i);
                }


            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));


         //Configura Conversas ref
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        conversaRef = databaseReference.child("conversas").child(identificadorUsuario);



        return  view;
    }

    public void recuperarConversar(){
        conversas.clear();
       eventeConversa = conversaRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Receuperando conversas
                Conversa conversa = snapshot.getValue(Conversa.class);
                conversas.add(conversa);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}