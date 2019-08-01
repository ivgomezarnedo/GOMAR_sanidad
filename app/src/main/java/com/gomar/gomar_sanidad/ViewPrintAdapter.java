package com.gomar.gomar_sanidad;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * This class handle the functionality to return a PDF file with the information of one or several Fichas
 */
@ReportsCrashes(mailTo = "ivangomezarnedo@gmail.com", customReportContent = {
        ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
        ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
        ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT},
        mode = ReportingInteractionMode.TOAST)
public class ViewPrintAdapter extends PrintDocumentAdapter {

    /**
     * An ArrayList of String with the identifiers of all the Fichas to store as PDF.
     */
    private ArrayList<String> listafichasByLote;
    /**
     * The object that will handle the connection with the DB.
     */
    private SQL sgbd = null;
    private PrintedPdfDocument mDocument;
    /**
     * Activity that has called to this class
     */
    private Context mContext;

    /**
     * Instantiates a new View print adapter.
     *
     * @param context     the context
     * @param listafichas the listafichas
     * @param sgbd        the sgbd
     */
    public ViewPrintAdapter(Context context,ArrayList<String> listafichas, SQL sgbd) {
        mContext = context;
        this.listafichasByLote=listafichas;
        this.sgbd = sgbd;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback, Bundle extras) {

        mDocument = new PrintedPdfDocument(mContext, newAttributes);

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                .Builder("Fichas_"+getTimestampFichero()+".pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(mDocument.getPages().size());

        PrintDocumentInfo info = builder.build();
        callback.onLayoutFinished(info, true);
    }

    /**
     * This method will populate the Activity_layout that will be printed to each PDF.
     * Most of the params defined in this method are not used (by me)
     * @param pages
     * @param destination
     * @param cancellationSignal
     * @param callback
     */
    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal,
                        WriteResultCallback callback) {
        //Layout Inflater permite instanciar un Layout, poblarlo y utilizarlo como si hubiese lanzado una actividad pero sin haberlo hecho.
        LayoutInflater inflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int paginas=0; //Variable que almacena el número de páginas a escribir/Escritas

        for (String lote : listafichasByLote) { //Para cada ficha.
            Ficha ficha = sgbd.getFichaByLote(lote);
            ArrayList<Componente> componentes = sgbd.getComponentesByFicha(lote);
            Componente componente1 = null;
            for (int i = 1; i <= componentes.size(); i++) { //Para cada componente procedemos a instanciar sus elementos en el Layout.
                /*
                Tenemos 3 posibles Layouts a instanciar:
                -Una nueva ficha: tendrá los datos de la ficha y un componente.
                -Dos componentes
                -Un componente.
                El código debe ser capaz de decidir cuándo instanciar una actividad y cuando instanciar otra.
                Decidí que la opción más sencilla era diseñar el Layout como si fuese una actividad, poblarlo con los datos de la ficha y componentes y
                imprimir dicho Layout a una hoja de PDF (como si de una captura de pantalla se tratase).
                 */
                if (i == 1 || i == 0) {
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(2250, 2400, 1).create();
                    PdfDocument.Page page = mDocument.startPage(pageInfo);
                    Componente componente=componentes.get(i - 1);
                    View view= inflater.inflate(R.layout.activity_pdf1,null,false); //El LayoutInflater (con la actividad instanciada) devuelve una vista.


                    ((AutoCompleteTextView)view.findViewById(R.id.nombreficha_pdf1)).setText(ficha.getNombre());
                    ((AutoCompleteTextView)view.findViewById(R.id.loteficha_pdf1)).setText(ficha.getLote());
                    ((EditText)view.findViewById(R.id.fechaconsumo_pdf1)).setText(ficha.getFecha_preferente());
                    ((EditText)view.findViewById(R.id.fechaelaboracion_pdf1)).setText(ficha.getFecha_elaboracion());
                    ((EditText)view.findViewById(R.id.kgficha_pdf1)).setText(ficha.getkg_Producto().toString());

                    ((AutoCompleteTextView)view.findViewById(R.id.nombre_pdf1)).setText(componente.getNombre());
                    ((AutoCompleteTextView)view.findViewById(R.id.lote_pdf1)).setText(componente.getLote());
                    ((AutoCompleteTextView)view.findViewById(R.id.proveedor_pdf1)).setText(componente.getProveedor());
                    ((EditText)view.findViewById(R.id.kg_pdf1)).setText(componente.getKg_Componente().toString());
                    ((TextView)view.findViewById(R.id.timestamp_pdf1)).setText(getTimestampFicha());

                    int measureWidth = View.MeasureSpec.makeMeasureSpec(page.getCanvas().getWidth(), View.MeasureSpec.EXACTLY); //Anchura de la actividad
                    int measuredHeight = View.MeasureSpec.makeMeasureSpec(page.getCanvas().getHeight(), View.MeasureSpec.EXACTLY); //Altura de la actividad

                    view.measure(measureWidth, measuredHeight);
                    view.layout(0, 0, page.getCanvas().getWidth(), page.getCanvas().getHeight());
                    view.draw(page.getCanvas());
                    mDocument.finishPage(page);

                } else if (((componentes.size() - i) > 1) && componente1 == null) {
                    componente1 = componentes.get(i - 1);
                } else if ((componentes.size() - i) > 1 && componente1 != null) {
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(2250, 1400, 1).create();
                    PdfDocument.Page page = mDocument.startPage(pageInfo);
                    Componente componente2=componentes.get(i - 1);
                    View view= inflater.inflate(R.layout.activity_pdf2,null,false);

                    ((AutoCompleteTextView)view.findViewById(R.id.nombre_1_pdf2)).setText(componente1.getNombre());
                    ((AutoCompleteTextView)view.findViewById(R.id.lote_1_pdf2)).setText(componente1.getLote());
                    ((AutoCompleteTextView)view.findViewById(R.id.proveedor_1_pdf2)).setText(componente1.getProveedor());
                    ((EditText)view.findViewById(R.id.kg_1_pdf2)).setText(componente1.getKg_Componente().toString());

                    ((AutoCompleteTextView)view.findViewById(R.id.nombre_2_pdf2)).setText(componente2.getNombre());
                    ((AutoCompleteTextView)view.findViewById(R.id.lote_2_pdf2)).setText(componente2.getLote());
                    ((AutoCompleteTextView)view.findViewById(R.id.proveedor_2_pdf2)).setText(componente2.getProveedor());
                    ((EditText)view.findViewById(R.id.kg_2_pdf2)).setText(componente2.getKg_Componente().toString());
                    ((TextView)view.findViewById(R.id.timestamp_pdf2)).setText(getTimestampFicha());
                    int measureWidth = View.MeasureSpec.makeMeasureSpec(page.getCanvas().getWidth(), View.MeasureSpec.EXACTLY);
                    int measuredHeight = View.MeasureSpec.makeMeasureSpec(page.getCanvas().getHeight(), View.MeasureSpec.EXACTLY);
                    view.measure(measureWidth, measuredHeight);
                    view.layout(0, 0, page.getCanvas().getWidth(), page.getCanvas().getHeight());
                    view.draw(page.getCanvas());
                    // finish the page
                    mDocument.finishPage(page);
                    componente1 = null;
                } else {
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(2250, 1400, 1).create();
                    PdfDocument.Page page = mDocument.startPage(pageInfo);
                    View view= inflater.inflate(R.layout.activity_pdf3,null,false);
                    AutoCompleteTextView nombre=((AutoCompleteTextView)view.findViewById(R.id.nombre_componente_pdf3));
                    nombre.setText(componentes.get(i - 1).getNombre());
                    ((AutoCompleteTextView)view.findViewById(R.id.lote_pdf3)).setText(componentes.get(i - 1).getLote());
                    ((AutoCompleteTextView)view.findViewById(R.id.proveedor_pdf3)).setText(componentes.get(i - 1).getProveedor());
                    ((EditText)view.findViewById(R.id.kg_pdf3)).setText(componentes.get(i - 1).getKg_Componente().toString());
                    ((TextView)view.findViewById(R.id.timestamp_pdf3)).setText(getTimestampFicha());
                    int measureWidth = View.MeasureSpec.makeMeasureSpec(page.getCanvas().getWidth(), View.MeasureSpec.EXACTLY);
                    int measuredHeight = View.MeasureSpec.makeMeasureSpec(page.getCanvas().getHeight(), View.MeasureSpec.EXACTLY);
                    view.measure(measureWidth, measuredHeight);
                    view.layout(0, 0, page.getCanvas().getWidth(), page.getCanvas().getHeight());
                    view.draw(page.getCanvas());
                    // finish the page
                    mDocument.finishPage(page);
                }
            }
        }

        try {
            paginas=mDocument.getPages().size();
            mDocument.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            mDocument.close();
            mDocument = null;
        }
        callback.onWriteFinished(new PageRange[]{new PageRange(0, paginas)});
    }

    /**
     * Method that returns the Timestamp for the FileName
     * @return a String with the new Timestamp
     */
    private String getTimestampFichero()
    {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        return formatter.format(date);
    }

    /**
     * Method that returns the Timestamp for each Ficha.
     * @return a String with the new Timestamp.
     */
    private String getTimestampFicha()
    {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        return formatter.format(date);
    }

}