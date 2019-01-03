package com.example.android.appdeinventario;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    InventarioCursorAdapter mInventarioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInventarioAdapter = new InventarioCursorAdapter(this,null);

        ListView inventarioListView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        inventarioListView.setEmptyView(emptyView);
        inventarioListView.setAdapter(mInventarioAdapter);


        inventarioListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetalhesActivity.class);
                Uri petUri = ContentUris.withAppendedId(ProdutoContract.ProdutoEntry.CONTENT_URI,id);
                intent.setData(petUri);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,EditorActivity.class));
            }
        });


        getLoaderManager().initLoader(0,null,this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] projection = {
                ProdutoContract.ProdutoEntry._ID,
                ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_NOME,
                ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_QUANTIDADE,
                ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_PRECO,
                ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_IMAGEM,
                ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_TELEFONE,
                ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_EMAIL
        };
        return new CursorLoader(this,
                ProdutoContract.ProdutoEntry.CONTENT_URI,
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null);                   // The sort order
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mInventarioAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mInventarioAdapter.swapCursor(data);
    }

    public void atualizarVenda(View view){
        ListView listView = (ListView) findViewById(R.id.list_view);
        int positionView=listView.getPositionForView(view);
        Cursor cursor = mInventarioAdapter.getCursor();
        cursor.moveToPosition(positionView);
        int idColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry._ID);
        int quantidadeColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_QUANTIDADE);
        int currentId = cursor.getInt(idColunaIndice);
        int currentQuantidade = cursor.getInt(quantidadeColunaIndice);
        if(currentQuantidade-1>=0) {
            ContentValues values = new ContentValues();
            values.put(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_QUANTIDADE, currentQuantidade - 1);
            getContentResolver().update(Uri.withAppendedPath(ProdutoContract.ProdutoEntry.CONTENT_URI, "" + currentId), values, null, null);
        }
    }

}
