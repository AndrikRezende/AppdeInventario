package com.example.android.appdeinventario;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Andrik on 26/04/2018.
 */

public class ProdutoContract {

    public final static String CONTENT_AUTHORITY = "com.example.android.appdeinventario";

    public final static Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public final static String PATH_PRODUTOS = "produtos";

    private ProdutoContract() {}

    public static final class ProdutoEntry implements BaseColumns{

        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PRODUTOS);

        public static final String CONTENT_LIST_TYPE
                = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUTOS;

        public static final String CONTENT_ITEM_TYPE
                = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUTOS;

        public final static String TABELA_NOME = "Produtos";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUNA_PRODUTO_NOME = "nome";

        public final static String COLUNA_PRODUTO_QUANTIDADE = "quantidade";

        public final static String COLUNA_PRODUTO_PRECO = "preco";

        public final static String COLUNA_PRODUTO_IMAGEM = "imagem";

        public final static String COLUNA_PRODUTO_TELEFONE = "telefone";

        public final static String COLUNA_PRODUTO_EMAIL = "email";

    }

}
