package com.gomar.gomar_sanidad;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


//TODO INICIALIZAR TODOS LOS TEXTVIEW EN EL MÉTODO ONCREATE (hacerlos  globales)

/**
 * The type Lista fichas.
 */
@ReportsCrashes(mailTo = "ivangomezarnedo@gmail.com", customReportContent = {
        ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
        ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
        ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT},
        mode = ReportingInteractionMode.TOAST)
public class Lista_Fichas extends AppCompatActivity {

     /*
     * The Fichaactual.
     */
    int fichaactual=0;
    /**
     * The Fecha consumo.
     */
    EditText fechaConsumo = null;
    /**
     * The Fecha elaboracion.
     */
    EditText fechaElaboracion = null;
    /**
     * The Listafichas.
     */
    ArrayList<String> listafichas=null;
    /**
     * The Calendar elaboracion.
     */
    final Calendar calendarElaboracion = Calendar.getInstance();
    /**
     * The Calendar consumo.
     */
    final Calendar calendarConsumo = Calendar.getInstance();
    private Button btn_crear;
    private Button btn_eliminar;
    /**
     * The Componentes.
     */
    int componentes = 0;
    /**
     * The Sgbd.
     */
    SQL sgbd = null;





    private View.OnClickListener btn_crearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ClickCrear();
        }
    };
    private View.OnClickListener btn_eliminarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ClickEliminar();
        }
    };


    //Generación de la activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sgbd = new SQL(this);
        listafichas=getIntent().getStringArrayListExtra("listafichas");
        setContentView(R.layout.activity_lista_fichas);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_lista_fichas);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fechaConsumo = (EditText) findViewById(R.id.TVFechaConsum_lf);
        fechaElaboracion = (EditText) findViewById(R.id.SeleccionarCampos_lf);
        updateLabelElaboracion();
        updateLabelPreferente();
        if(listafichas.size()>0)
        {
            setTitle("Ficha "+(fichaactual+1)+ " de "+listafichas.size());
            popularFicha(fichaactual);

        }
        else
            vaciarActivity();

/*
Funcionalidad para poblar los TextView de la fecha y para actualizar su valor según lo que el usuario seleccione de los calendarios que se muestran como pop-up.
 */

        final DatePickerDialog.OnDateSetListener dateElaboracion = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendarElaboracion.set(Calendar.YEAR, year);
                calendarElaboracion.set(Calendar.MONTH, monthOfYear);
                calendarElaboracion.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelPreferente();
            }

        };

        final DatePickerDialog.OnDateSetListener dateConsumo = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendarConsumo.set(Calendar.YEAR, year);
                calendarConsumo.set(Calendar.MONTH, monthOfYear);
                calendarConsumo.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelElaboracion();
            }

        };

        fechaConsumo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Lista_Fichas.this, dateConsumo, calendarConsumo
                        .get(Calendar.YEAR), calendarConsumo.get(Calendar.MONTH),
                        calendarConsumo.get(Calendar.DAY_OF_MONTH)).show();
                updateLabelElaboracion();
            }
        });

        fechaElaboracion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Lista_Fichas.this, dateElaboracion, calendarElaboracion
                        .get(Calendar.YEAR), calendarElaboracion.get(Calendar.MONTH),
                        calendarElaboracion.get(Calendar.DAY_OF_MONTH)).show();
                updateLabelPreferente();

            }
        });


    }

    private void updateLabelElaboracion() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        fechaConsumo.setText(sdf.format(calendarConsumo.getTime()));

    }
    private void updateLabelPreferente() {
        /*
        Aquí cogeremos la primera fecha de consumo preferente que se introdujo para la creación de la ficha patrón, calcularemos los días desde el día actual
        hasta dicha fecha y siempre que generemos una ficha similar le sumaremos esos días con respecto al día de creación del elaborado.
         */
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        fechaElaboracion.setText(sdf.format(calendarElaboracion.getTime()));

    }

    /**
     * In this method it's generated a LinearLayout to store the information of each Componente.
     * Then it's created each one of the components of each Componente and all of them are added to the previous LinearLayout.
     */
    private void ClickCrear() {
        LinearLayout layout_comp = (LinearLayout) findViewById(R.id.layout_componentes_lf);
        layout_comp.setOrientation(LinearLayout.VERTICAL);
        LayoutParams layoutparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);


        LayoutParams LayoutParamsview = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        //Creating textview .
        componentes++;
        TextView textViewComp=new TextView(this);
        textViewComp.setText("Componente "+componentes);
        textViewComp.setTag("Texto"+componentes);
        AutoCompleteTextView auto_nombre = new AutoCompleteTextView(this);
        auto_nombre.setHint("Introduce el nombre del componente "+componentes);
        auto_nombre.setTag("Nombre"+componentes);
        AutoCompleteTextView auto_proveedor = new AutoCompleteTextView(this);
        auto_proveedor.setTag("Proveedor"+componentes);
        auto_proveedor.setHint("Introduce el nombre del proveedor "+componentes);
        AutoCompleteTextView auto_lote = new AutoCompleteTextView(this);
        auto_lote.setTag("Lote"+componentes);
        auto_lote.setHint("Introduce el lote del componente "+componentes);
        AutoCompleteTextView auto_peso = new AutoCompleteTextView(this);
        auto_peso.setTag("Peso"+componentes);
        auto_peso.setHint("Introduce el peso del componente "+componentes);

        textViewComp.setLayoutParams(LayoutParamsview);
        auto_nombre.setLayoutParams(LayoutParamsview);
        auto_proveedor.setLayoutParams(LayoutParamsview);
        auto_lote.setLayoutParams(LayoutParamsview);
        auto_peso.setLayoutParams(LayoutParamsview);

        //Adding textview to linear layout using Add View function.
        layout_comp.addView(textViewComp);
        layout_comp.addView(auto_nombre);
        layout_comp.addView(auto_proveedor);
        layout_comp.addView(auto_lote);
        layout_comp.addView(auto_peso);

    }

    /**
     * This method will remove the TextViews dinamically created to store the Componente information.
     * Each time that we click the button the last components added will be removed.
     */
    private void ClickEliminar() {
        if(componentes!=0)
        {
            LinearLayout layout_comp = (LinearLayout) findViewById(R.id.layout_componentes_lf);
            layout_comp.removeView(layout_comp.findViewWithTag(("Texto"+componentes)));
            layout_comp.removeView(layout_comp.findViewWithTag(("Nombre"+componentes)));
            layout_comp.removeView(layout_comp.findViewWithTag(("Proveedor"+componentes)));
            layout_comp.removeView(layout_comp.findViewWithTag(("Lote"+componentes)));
            layout_comp.removeView(layout_comp.findViewWithTag(("Peso"+componentes)));
            componentes--;
        }

    }
    @Override //Hace lo mismo que un listener para capturar el click en el botón de volver atrás.
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lista_fichas, menu);
        return true;
    }

//Este método gestiona la selección de los elementos del SubMenú que surge al hacer click en el botón de arriba a la derecha. Llama a los diferentes métodos
    //que contienen la funcionalidad para cada tipo de elemento seleccionado.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Boolean resultado=true;
        switch (item.getItemId()) {
            case R.id.back_list:
                clickAdelanteAtras("Atras");
                return true;
            case R.id.forward_list:
                clickAdelanteAtras("adelante");
                return true;
            case R.id.delete_list:
                eliminaFicha();
                Toast.makeText(this, "Ficha correctamente eliminada de la BD", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.exportar1:
                //Lo hago así para no tener que declarar otro método diferente con el caso de que solo le pasemos una sola ficha.
                ArrayList<String> fichaseleccionada=new ArrayList<>();
                fichaseleccionada.add(listafichas.get(fichaactual));
                printPDF(fichaseleccionada);
                Toast.makeText(this, "Ficha correctamente persistida en PDF", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.exportarTodo:
                printPDF(listafichas);
                Toast.makeText(this, "Las fichas resultado de la búsqueda han sido correctamente persistidas a PDF.", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *  This method calls to the interface to generate PDFs. We submit as parameter the list of Fichas to print.
     *
     * @param listafichasseleccionadas the listafichasseleccionadas
     */
    public void printPDF(ArrayList<String> listafichasseleccionadas) {
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        printManager.print("print_any_view_job_name", new ViewPrintAdapter(this,
                listafichasseleccionadas , sgbd),null);
    }

    /**
     * This method handle the functionality to change between Fichas. Forward or back (the direction will be the one submitted as parameter ("adelante" o "atras")
     *
     * @param sentido the sentido
     */
    public void clickAdelanteAtras(String sentido)
    {
        //Defino un caso especial para cuando solo hay una única ficha en el resultado de la búsquda.
        boolean specialcase=false;
        if(listafichas.size()==1)
            specialcase=true;
        else
            fichaactual=(sentido.equals("adelante"))?(fichaactual+1):(fichaactual-1);
        if(fichaactual+1!=1 && !specialcase)
        {
            ((ActionMenuItemView)findViewById(R.id.back_list)).setEnabled(true);
        }
        else
        {
            ((ActionMenuItemView)findViewById(R.id.back_list)).setEnabled(false);
        }

        if(fichaactual+1!=listafichas.size() && !specialcase)
        {
            ((ActionMenuItemView)findViewById(R.id.forward_list)).setEnabled(true);
        }
        else
        {
            ((ActionMenuItemView)findViewById(R.id.forward_list)).setEnabled(false);
        }
        if(!specialcase)
        {
            setTitle("Ficha "+(fichaactual+1)+ " de "+listafichas.size());
            popularFicha(fichaactual);
        }

    }

    /**
     * Popular ficha.
     *
     * @param numelem the numelem
     */
    public void popularFicha(int numelem)
    { //Popula la ventana con los datos de la Ficha
        Ficha ficha=sgbd.getFichaByLote(listafichas.get(numelem));
        ArrayList<Componente> listacomponentes=sgbd.getComponentesByFicha(ficha.getLote());
        if(ficha==null)
            System.out.println("Algo no ha ido como debería");
        else
        {
            TextView TVNombre = (TextView) findViewById(R.id.TVNombrePrep_lf);
            TVNombre.setFocusable(false);
            TextView TVLote = (TextView) findViewById(R.id.TVLote_lf);
            TVLote.setFocusable(false);
            TextView TVKg = (TextView) findViewById(R.id.TVKg_lf);
            TVKg.setFocusable(false);
            TextView TVFechaElab = (TextView) findViewById(R.id.SeleccionarCampos_lf);
            TVFechaElab.setFocusable(false);
            TextView TVFechaConsum = (TextView) findViewById(R.id.TVFechaConsum_lf);
            TVFechaConsum.setFocusable(false);
            TVNombre.setText(ficha.getNombre());
            TVFechaConsum.setText(ficha.getFecha_preferente());
            TVKg.setText(ficha.getkg_Producto().toString());
            updateLabelPreferente();
            updateLabelElaboracion();
            TVLote.setText(ficha.getLote());
            LinearLayout layout_comp = (LinearLayout) findViewById(R.id.layout_componentes_lf);
            TextView TVComNom = null;
            TextView TVComProv = null;
            TextView TVComLote = null;
            TextView TVComKg = null;
            while (componentes>0)
                ClickEliminar();
            for (Componente componente:listacomponentes) {
                ClickCrear();
                TVComNom = (TextView) layout_comp.findViewWithTag(("Nombre"+componentes));
                TVComNom.setFocusable(false);
                TVComProv = (TextView) layout_comp.findViewWithTag(("Proveedor"+componentes));
                TVComProv.setFocusable(false);
                TVComLote = (TextView) layout_comp.findViewWithTag(("Lote"+componentes));
                TVComLote.setFocusable(false);
                TVComKg = (TextView) layout_comp.findViewWithTag(("Peso"+componentes));
                TVComKg.setFocusable(false);
                TVComNom.setText(componente.getNombre());
                TVComProv.setText(componente.getProveedor());
                TVComLote.setText(componente.getLote());
                TVComKg.setText(componente.getKg_Componente().toString());
            }

        }

    }

    /**
     * THis method will be executed when we have no Fichas left. In that case it only go back to the previous activity.
     */
    public void vaciarActivity()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sin resultados");

        // add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onSupportNavigateUp();

            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * This method will remove a Ficha of the DB and also for the ones that we show to the user. First it is removed a Ficha from the DB.
     * Then it's changed the value of @fichaactual (the variable that stores information about the Ficha selected (the one that the user is viewing at)
     */
    public void eliminaFicha()
    {
        TextView TVLote = (TextView) findViewById(R.id.TVLote_lf);
        sgbd.removeFichaByLote(TVLote.getText().toString());
        System.out.println(listafichas.remove(TVLote.getText().toString()));
        fichaactual=(listafichas.size()==0 || fichaactual==0)?0:fichaactual-1;
        if(listafichas.size()==0)
        {//Si no quedan más fichas
            vaciarActivity();
        }
        else
        {
            setTitle("Ficha "+(fichaactual+1)+ " de "+listafichas.size()); //Cambiamos el título de la activity.
            popularFicha(fichaactual); //Llamamos al método para popular la activity con una Ficha (determinada por la fichaactual).
        }



    }
}

