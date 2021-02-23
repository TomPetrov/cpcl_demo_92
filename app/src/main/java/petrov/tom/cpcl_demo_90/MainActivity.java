package petrov.tom.cpcl_demo_90; import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import android.view.View;
import android.widget.ImageView;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory; public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @RequiresApi(api = Build.VERSION_CODES.Q) @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try { bitmap = getCompressedBitmapFromMemory(); } catch  (Exception e) { e.printStackTrace(); }
        ((ImageView)(findViewById(R.id.imageView))).setImageBitmap(bitmap);
        findViewById(R.id.cpcl).setOnClickListener(this::onClick);
    }

    /**comment out one of three lines**/
    @Override public void onClick(View view) {
//        workLabelCPCL();
        //workBitmap(getCompressedBitmapFromMemory());
        workLabelZPL();
    }

    /**tompe@mda.org.il 21/2/2020*/

    //region connection
    final static String macAddressZQ220="a4:da:32:83:d7:47";
    final static String macAddressZQ520="c4:64:e3:93:5d:ea";

    final static String macAddress=macAddressZQ520;
    protected ZebraPrinter printer;
    private void transmitCPCL(String cpcl) { try {

        // Establish connection
        Connection thePrinterConn = new BluetoothConnection(macAddress);
        thePrinterConn.open();
        printer = ZebraPrinterFactory.getInstance(thePrinterConn);



        // Send the data to printer as a byte array
        thePrinterConn.write(cpcl.getBytes("Cp1255"));

        // Make sure the data got to the printer before closing the connection
        Thread.sleep(5000);

        // Close the connection to release resources
        thePrinterConn.close();

    } catch (Exception e) { e.printStackTrace(); } }
    private void transmitZPL(String zpl) { try {

        // Establish connection
        Connection thePrinterConn = new BluetoothConnection(macAddress);
        thePrinterConn.open();
        printer = ZebraPrinterFactory.getInstance(thePrinterConn);



        thePrinterConn.write(zpl.getBytes());

        // Make sure the data got to the printer before closing the connection
        Thread.sleep(5000);

        // Close the connection to release resources
        thePrinterConn.close();

    } catch (Exception e) { e.printStackTrace(); } }
    //endregion

    //region bitmap
    Bitmap bitmap= null;
    public static String imageFilePath = "/storage/emulated/0/Download/knesset_dithered_v9.jpg";
    private Bitmap m_bmp;
    private ParseBitmap m_BmpParser;
    public void workBitmap(Bitmap bitmap) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inDither = false;
//        options.outColorSpace//incolorspace


        m_bmp = bitmap;

        m_BmpParser = new ParseBitmap(m_bmp);
        m_BmpParser.logData();
        transmitCPCL(
                "! 0 200 200 230 1\r\n"
                        + m_BmpParser.ExtractGraphicsDataForCPCL(0,0)+"\r\n"
                        +"PRINT\r\n");
        try {
//            Looper.prepare();
            Connection thePrinterConn = new BluetoothConnection(macAddress);
            thePrinterConn.open();
            printer = ZebraPrinterFactory.getInstance(thePrinterConn);


            String str = m_BmpParser.ExtractGraphicsDataForCPCL(0,0);
            String cpcl = "! 0 200 200 230 1\r\n"
                    + str
                    +"PRINT\r\n";

            // Send the data to printer as a byte array.
            thePrinterConn.write(cpcl.getBytes());

            //Make sure the data got to the printer before closing the connection
            Thread.sleep(5000);

            // Close the connection to release resources.
            thePrinterConn.close();

            //Looper.myLooper().quit();
        } catch (Exception e) { e.printStackTrace(); }
    }
    public static Bitmap getCompressedBitmapFromMemory() {

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;


        BitmapFactory.decodeFile(imageFilePath, options);

        Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math
                .abs(options.outWidth - 100);
        if (options.outHeight * options.outWidth * 2 >= 16384) {
            double sampleSize = scaleByHeight
                    ? options.outHeight / 100
                    : options.outWidth / 100;
            options.inSampleSize =
                    (int) Math.pow(2d, Math.floor(
                            Math.log(sampleSize) / Math.log(2d)));
        }
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[512];


        Bitmap output = BitmapFactory.decodeFile(imageFilePath, options);
        if(output==null){
            Log.e("bitmap factory","null object");}
        return output;
    }
    //endregion

    String barcode = "0987654321";

    String id = "211717459";
    String surname = "בורטפ";
    String firstname = "םות";
    String maidenname = "";
    String birthdate = "21/2/2000";

    String regioncode = "001";
    String votingbooth = "003";

    //region label cpcl zq220
    final static String EG_LEFT_OLIVE = "0000040006000E000F000D000D000D008C30CC30E470E4F0D1B0C1206360624036001C308430DF70EEF0F7E0F7A047606740260016000E008430D5F0EF70E6F0F6B044A06720634036401E00060012C01AC00E00070003000100018000E0007800380008"+"\n";
    final static String EG_RIGHT_OLIVE = "0000010003000380058005800580058041886198713879386C58241834301230026061C0610877D87BB83B782D583710173003600340038061086D5877B87B3869582D5025301630136003C003001A401AC002800700060004000C003800D000E0008000\n";
    final static String EG_TOP_LOGO = "000000000000000000000000000000038000000000000000000002A00000000000000000000550000000000000000000043C0000000000000000000943000000000000000000049480000000000000000015496000000000000000001224B8000000000000000029551600000000000000000492AB00000000000000006A489DC00000000000000092AA4AB0000000000000004925576C000000000000012A924DB40000000000000149492ADC00000000000002542A9F680000000000000122A44AD8000000000000054A92AD600000000000000A294A57B000000000000004A4529AC000000000000012932956A000000000000014A8945BC0000000000000224AA2B6800000000000002AA74A5B0000000000000097FFFFED00000000000000FFFFFFFF000000000000BFFFFFFFFFFF000000017FFFFFFFFFFFFFFF8000FFFFFFFFD003FFFFFFFF80FFFFFFC0000001FFFFFF80FFFF800002200000FFFF80FF00000622333000007F80000006622063333000000000044222223333333000000CC822222263333311980008044462203333333998000880222222633333111800084A2222223333333198000884462622333331199800080022622063333331180008AA2222223333331198000840222222333333399800088442222063333311180008442462223333333198000880222222633333199800080A42222033333331180008A42462226333331198000880222622333333399800084442222033333311180004442662226333313190000000422222333333100000D000002222333310000280FFE800002063000005FF80FFFFF00000000007FFFF80FFFFFFFA00001FFFFFFF803FFFFFFFFD3FFFFFFFFD00001FFFFFFFFFFFFFFA000000001FFFFFFFFFF40000000000000FFFFFF00000000000000000FFF70000000000";
    final static String TEXT_BOTTOM_TITLE = ("24-ה  תסנכל  תיזכרמה  תוריחבה  תדעו");




    public void workLabelCPCL(){
        String cpcl =
                //header
                "! 0 200 200 426 1\n"

                //logo
                +"EG 11 61 245 3 "+ EG_TOP_LOGO +"\n"

                //olives * * X Y
                +"EG 2 50 10 330 "+ EG_LEFT_OLIVE +"\n"
                +"EG 2 50 550 330 "+ EG_RIGHT_OLIVE +"\n"
                +"TEXT SWISIL22.CPF 0 52 337 "+ TEXT_BOTTOM_TITLE +"\n"
                +"TEXT SWISIL22.CPF 0 50 337 "+ TEXT_BOTTOM_TITLE +"\n"

//                +"TEXT SWISIL22.CPF 0 51 335 "+ TEXT_BOTTOM_TITLE +"\n"
//                +"TEXT SWISIL22.CPF 0 51 339 "+ TEXT_BOTTOM_TITLE +"\n"





                        //barcode
                +"TEXT SWISIL18.CPF 0 21 95 "+barcode+"\n"
                +"VBARCODE 128 1 2 120 30 305 "+barcode+"\r\n"

                //lines
                + getCPCLLines()



                //footer
                +"PRINT\r\n";
        Log.e("cpcl",cpcl);
        transmitCPCL(cpcl);
    }
    public String getCPCLLines(){
        int stringYPosStart = 86;
        final int stringYPosIncrement = 40;

        int stringYpos = stringYPosStart;
        StringBuilder builder = new StringBuilder();

        builder.append("TEXT SWISIL18.CPF 0 495 "+stringYpos+" "+ ":.ז.ת" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 494 "+stringYpos+" "+ ":.ז.ת" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 184 "+stringYpos+" "+ id +"\n");
        stringYpos+=stringYPosIncrement;

        builder.append("TEXT SWISIL18.CPF 0 423 "+stringYpos+" "+ ":החפשמ םש" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 422 "+stringYpos+" "+ ":החפשמ םש" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 184 "+stringYpos+" "+ surname +"\n");
        stringYpos+=stringYPosIncrement;

        if(maidenname!=null && !maidenname.isEmpty()){
            builder.append("TEXT SWISIL18.CPF 0 437 "+stringYpos+" "+ ":םירוענ םש" +"\n");
            builder.append("TEXT SWISIL18.CPF 0 436 "+stringYpos+" "+ ":םירוענ םש" +"\n");
            builder.append("TEXT SWISIL18.CPF 0 184 "+stringYpos+" "+ maidenname +"\n");
            stringYpos+=stringYPosIncrement;
        }

        builder.append("TEXT SWISIL18.CPF 0 451 "+stringYpos+" "+ ":יטרפ םש" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 450 "+stringYpos+" "+ ":יטרפ םש" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 184 "+stringYpos+" "+ firstname +"\n");
        stringYpos+=stringYPosIncrement;

        builder.append("TEXT SWISIL18.CPF 0 429 "+stringYpos+" "+ ":הדיל ךיראת" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 428 "+stringYpos+" "+ ":הדיל ךיראת" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 184 "+stringYpos+" "+ birthdate +"\n");
        stringYpos+=stringYPosIncrement;


        //voting booth and region code


        if(maidenname==null || maidenname.isEmpty()){stringYpos+=stringYPosIncrement;}
        builder.append("TEXT SWISIL18.CPF 0 253 "+stringYpos+" "+ ":יפלק .סמ" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 252 "+stringYpos+" "+ ":יפלק .סמ" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 385 "+stringYpos+" "+ regioncode +"\n");


        builder.append("TEXT SWISIL18.CPF 0 457 "+stringYpos+" "+ ":בושי דוק" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 456 "+stringYpos+" "+ ":בושי דוק" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 183 "+stringYpos+" "+ votingbooth +"\n");



        builder.append("PATTERN 106"+"\n");
        //cmd, start x, start y, end x, end y, height?
        builder.append("LINE 180 115 541 115 3"+"\n");
        builder.append("LINE 180 155 541 155 3"+"\n");
        builder.append("LINE 180 195 541 195 3"+"\n");
        builder.append("LINE 180 235 541 235 3"+"\n");

        if(maidenname!=null && !maidenname.isEmpty())
        builder.append("LINE 180 275 541 275 3"+"\n");


        builder.append("LINE 180 315 346 315 3"+"\n");
        builder.append("LINE 385 315 541 315 3"+"\n");
//        builder.append("LINE 195 305 556 305 3"+"\n"); combined two lines
//        165 total length of both halves



        return builder.toString();
    }




    //endregion

    //region label zpl zq520
    String staticGraphics = "\u0010CT~~CD,~CC^~CT~\n" +
//            "^XA~TA000~JSN^LT0^MNW^MTD^PON^PMN^LH0,0^JMA^PR5,5~SD10^JUS^LRN^CI0^XZ\n" +

            "^XA\n" +
            "^MMT\n" +
            "^PW812\n" +
            "^LL0376\n" +
            "^LS0\n" +

            "^CI28\n" +
            "^CW7,E:TT0003M_.TTF^FS\n" +


            //
//            "^CW7,E:TT0003M_.TTF\n"+
            //
            "^FO672,128^GFA,01536,01536,00016,:Z64:\n" +
            "eJztU7FuwjAQdVIQiAU8wZChYgwTZWRxJdMZJDL3GzJ1jTpZDMyMiAm5EnwCDFW/oP2FqmJgahckMOfYLrZVVMYOPSFdnt69e2f7QOhPBPHw3MNDD/PMgQF1G4R86tbjmgMrnDu4QHs+7zQIMO7bOOZugwKlQ5+3G+AaHtgTAs3tI1JKk/55HuyxzYfc6y/15Dwf1DAeWOUVyWfO+L3EG98+H9hj+3zc42ny1rP5JyGEPf4D4M+Tu8jjwBQs3iss9vkJruTnbraSSY5YgvwKuQ55DbkqxCZDoF0qXMpZ+f6J4mGwJht3oKK1NvMj2m6Y+wnZKGbj7mQbM305NEA0jV5StSNNNpL6yXvH8KC/azfg5/BbzRNECOgbz2mk+Pljky08PT3pY+PfNXrjH13kP/vZP3L8c/1E68lv/reX+esLNP7XOSqoDUjl/corru6mKCwvbjIUrtT7fKkq2AL9frAbZTQS5v3yly8KjcOl/JIbJDbKr6734/sPkFeIj8xgKd4TZEXrMEX/cUkcAUpv7Dc=:180D\n" +
            "^FO0,288^GFA,00384,00384,00004,:Z64:\n" +
            "eJy1kL0KgWEYhi8+P1+KUgbLV18mo5RCKWI0mmVwAsrPKpPJZDIbTGaTDA7AETgWlzgDeevqfu73vZ/3D/41Em/W0oWkdaAGavbr+2rFYlaAprRKEFdgPoVaDMUhhCHk3/kN9FKu72A1hvYVOnuoHqFhvn4yfzPveWk1qfYktm+xdP3s/k/zB/3IefuKAyhvIbpDxmxWkNRDf9E/Pj7hfkzEe+C9sJ+cRHwelP7ll147th0K:72DC\n" +
            "^FO768,288^GFA,00384,00384,00004,:Z64:\n" +
            "eJytkDEKwkAURGezKhJFE7Cxka1SpLC23OBFPIq9YC1Wqa2srSysPIPFliFewodZwV4XhmH4bz6flf79hl/eQws0Q0XMy86Twsnidh9kbhvZUMtU+ImceekcpPwq7bbSFC6Fl+vmjyDb4JWX+fAX2Bx2hMbsf/P0njXO3iMd9ictvPeyZewd4OeAabz7Tl7jZbx7hTLk4pyzDLUBWJ84+e23XsNLIn8=:04C3\n" +
            "^FO160,96^GFA,02048,02048,00064,:Z64:\n" +
            "eJxjYBgFo2AUkAu4tLi6mhYtWLFCixwGAwOfvXz/8ccff/ypI4dBuf5RMApGwcgFADHXa/E=:9622\n" +
            "^FO160,128^GFA,02048,02048,00064,:Z64:\n" +
            "eJxjYBgFo2AUjIJRMApGAamAS4urq2nRghUrtMhhMDDw2cv3H3/88cefOnIYlOsHAFiHa/E=:E058\n" +
            "^FO160,192^GFA,02048,02048,00064,:Z64:\n" +
            "eJxjYBgFo2DoAi4trq6mRQtWrNAih8HAwGcv33/88ccff+rIYVCufxSMglEwCgYKAAAtAWvx:D83C\n" +
            "^FO448,288^GFA,00896,00896,00028,:Z64:\n" +
            "eJxjYBgyYNGCFSu0tLi6mlAZYLnHH3/8qbOX7z+OyiAoNwpGwYACABZfKyk=:5A82\n" +
            "^FO160,32^GFA,04096,04096,00064,:Z64:\n" +
            "eJxjYBgFo2AUjIJRMApGwVADXFpcXU2LFqxYoUUOg4GBz16+//jjjz/+1JHDoFz/KBgFo2AUjIJRMAroDwAX/Wvx:DFB1\n" +
            "^FO160,288^GFA,00768,00768,00024,:Z64:\n" +
            "eJxjYKAt4NLi6mpatGDFCi0UxgI+e/n+448//vhTh8J4gEucxs4cBSMUAACU3CcP:8941\n" +
            "^BY2,3,88^FT140,275^BCB,,Y,Y\n" +
            "^FD>;"+barcode+"^FS\n" +
            "^PQ1,0,1,Y";
    String closeLabel = "^XZ\n";

    public void workLabelZPL(){
        String zpl = staticGraphics

                // {x,y}
                + "^FT68,359^A7N,40,47 ^FB800,0,0,L,0^FD 24-ה תסנכל תיזכרמה תוריחבה תדעו^FS" // footer text
                //+ getZPLLines()
                + closeLabel;
        Log.e("cpcl",zpl);
        transmitZPL(zpl);
    }
    public String getZPLLines(){
        int stringYPosStart = 86;
        final int stringYPosIncrement = 40;

        int stringYpos = stringYPosStart;
        StringBuilder builder = new StringBuilder();

        builder.append("TEXT SWISIL18.CPF 0 495 "+stringYpos+" "+ ":.ז.ת" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 494 "+stringYpos+" "+ ":.ז.ת" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 184 "+stringYpos+" "+ id +"\n");
        stringYpos+=stringYPosIncrement;

        builder.append("TEXT SWISIL18.CPF 0 423 "+stringYpos+" "+ ":החפשמ םש" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 422 "+stringYpos+" "+ ":החפשמ םש" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 184 "+stringYpos+" "+ surname +"\n");
        stringYpos+=stringYPosIncrement;

        if(maidenname!=null && !maidenname.isEmpty()){
            builder.append("TEXT SWISIL18.CPF 0 437 "+stringYpos+" "+ ":םירוענ םש" +"\n");
            builder.append("TEXT SWISIL18.CPF 0 436 "+stringYpos+" "+ ":םירוענ םש" +"\n");
            builder.append("TEXT SWISIL18.CPF 0 184 "+stringYpos+" "+ maidenname +"\n");
            stringYpos+=stringYPosIncrement;
        }

        builder.append("TEXT SWISIL18.CPF 0 451 "+stringYpos+" "+ ":יטרפ םש" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 450 "+stringYpos+" "+ ":יטרפ םש" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 184 "+stringYpos+" "+ firstname +"\n");
        stringYpos+=stringYPosIncrement;

        builder.append("TEXT SWISIL18.CPF 0 429 "+stringYpos+" "+ ":הדיל ךיראת" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 428 "+stringYpos+" "+ ":הדיל ךיראת" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 184 "+stringYpos+" "+ birthdate +"\n");
        stringYpos+=stringYPosIncrement;


        //voting booth and region code


        if(maidenname==null || maidenname.isEmpty()){stringYpos+=stringYPosIncrement;}
        builder.append("TEXT SWISIL18.CPF 0 253 "+stringYpos+" "+ ":יפלק .סמ" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 252 "+stringYpos+" "+ ":יפלק .סמ" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 385 "+stringYpos+" "+ regioncode +"\n");


        builder.append("TEXT SWISIL18.CPF 0 457 "+stringYpos+" "+ ":בושי דוק" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 456 "+stringYpos+" "+ ":בושי דוק" +"\n");
        builder.append("TEXT SWISIL18.CPF 0 183 "+stringYpos+" "+ votingbooth +"\n");



        builder.append("PATTERN 106"+"\n");
        //cmd, start x, start y, end x, end y, height?
        builder.append("LINE 180 115 541 115 3"+"\n");
        builder.append("LINE 180 155 541 155 3"+"\n");
        builder.append("LINE 180 195 541 195 3"+"\n");
        builder.append("LINE 180 235 541 235 3"+"\n");

        if(maidenname!=null && !maidenname.isEmpty())
            builder.append("LINE 180 275 541 275 3"+"\n");


        builder.append("LINE 180 315 346 315 3"+"\n");
        builder.append("LINE 385 315 541 315 3"+"\n");
//        builder.append("LINE 195 305 556 305 3"+"\n"); combined two lines
//        165 total length of both halves



        return builder.toString();
    }


    //endregion


}

