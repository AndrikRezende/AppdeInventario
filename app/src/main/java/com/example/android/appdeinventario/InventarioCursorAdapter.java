package com.example.android.appdeinventario;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Andrik on 05/05/2018.
 */

public class InventarioCursorAdapter extends CursorAdapter{

    public InventarioCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        int nomeColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_NOME);
        int quantidadeColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_QUANTIDADE);
        int precoColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_PRECO);
        String currentNome = cursor.getString(nomeColunaIndice);
        int currentQuantidade = cursor.getInt(quantidadeColunaIndice);
        int currentPreco = cursor.getInt(precoColunaIndice);
        TextView nomeTextView = (TextView) view.findViewById(R.id.list_nome);
        TextView quantidadeTextView = (TextView) view.findViewById(R.id.list_quantidade);
        TextView precoTextView = (TextView) view.findViewById(R.id.list_preco);
        nomeTextView.setText(currentNome);
        quantidadeTextView.setText(""+currentQuantidade+" itens");
        precoTextView.setText(""+currentPreco+" reais");


    }


}
