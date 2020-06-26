package controladoria.save.irremote;

import android.hardware.ConsumerIrManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // IR Commands for Samsung TV UE32H...
    // from http://www.remotecentral.com/cgi-bin/codes/samsung/tv_functions/

    private final static String CMD_TV_POWER = //LIGA CHUVEIRO
            "0000 006C 0012 0000 0019 0030 0019 0019 0019 0019 0019 0019 0019 0030 0019 0030 0019 0030 0019 0030 0019 0030 0019 0030 0019 0030 0019 0019 0019 0019 0019 0019 0019 0019 0019 0019 0019 0030 0019 0002";
    private final static String CMD_TV_TEMPMAIS = //TEMPERATURA MAIS
            "0000 006C 0012 0000 001B 0030 0017 0030 001B 0017 001B 0030 0017 0017 001B 0017 0017 0017 001B 0017 001B 0017 001B 0017 001B 0030 001B 0017 0017 0030 001B 0017 0017 0017 001B 0017 0017 0017 001B 0002";
    private final static String CMD_TV_TEMPMENOS = //DIMINUI TEMPERATURA
            "0000 006C 0012 0000 001B 002F 0018 002F 001B 0018 001B 0018 001B 002F 001B 002F 0018 002F 001B 002F 0018 002F 001B 002F 001B 0018 001B 002F 001B 002F 0018 002F 001B 0018 0018 002F 001B 002F 001B 0002";

    private final static String CMD_TV_CH_NEXT = //PASSA RADIO MUSICA
            "0000 006C 0012 0000 0019 0030 0019 0030 0019 0019 0019 0030 0019 0030 0019 0030 0019 0030 0019 0030 0019 0030 0019 0019 0019 0019 0019 0030 0019 0030 0019 0030 0019 0030 0019 0019 0019 0030 0019 0002";
    private final static String CMD_TV_CH_PREV =
            "0000 006C 0012 0000 0019 0030 0019 0019 0019 0030 0019 0019 0019 0030 0019 0030 0019 0030 0019 0030 0019 0030 0019 0030 0019 0030 0019 0030 0019 0030 0019 0030 0019 0030 0019 0019 0019 0019 0019 0002";

    private final static String CMD_SB_VOLUP = //AUMENTA E DIMINUI O VOLUME
            "0000 006C 0012 0000 0019 0031 0019 0031 0019 0031 0019 0031 0019 0031 0019 0031 0019 0031 0019 0031 0019 0031 0019 0019 0019 0019 0019 0019 0019 0019 0019 0019 0019 0019 0019 0019 0019 0019 0019 0002";
    private final static String CMD_SB_VOLDOWN =
            "0000 006C 0012 0000 0019 002F 0019 002F 0019 002F 0019 0019 0019 002F 0019 002F 0019 002F 0019 002F 0019 002F 0019 002F 0019 0019 0019 0019 0019 0019 0019 0019 0019 002F 0019 002F 0019 0019 0019 0002";

    private final static String CMD_SB_SOURCE = // PEN DRIVE OU RADIO
            "0000 006C 0012 0000 0019 0031 0019 0031 0019 0031 0019 0031 0019 0031 0019 0031 0019 0031 0019 0031 0019 0019 0019 0019 0019 0019 0019 0031 0019 0031 0019 0019 0019 0019 0019 0019 0019 0031 0019 0002";
    // from http://irdb.tk/codes/

    private final static String CMD_SB_POWER =//AUMENTO RAPIDO DE TEMPERATURA
            "0000 006C 0012 0000 0019 0030 0019 0019 0019 0030 0019 0030 0019 0030 0019 0030 0019 0030 0019 0030 0019 0030 0019 0019 0019 0030 0019 0030 0019 0030 0019 0030 0019 0019 0019 0030 0019 0019 0019 0002";


    private ConsumerIrManager irManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        irManager = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);

        findViewById(R.id.tvpower).setOnClickListener(new ClickListener(hex2ir(CMD_TV_POWER)));
        findViewById(R.id.tempMaior).setOnClickListener(new ClickListener(hex2ir(CMD_TV_TEMPMAIS)));
        findViewById(R.id.TempMenor).setOnClickListener(new ClickListener(hex2ir(CMD_TV_TEMPMENOS)));
        findViewById(R.id.tvchnext).setOnClickListener(new ClickListener(hex2ir(CMD_TV_CH_NEXT)));
        findViewById(R.id.tvchprev).setOnClickListener(new ClickListener(hex2ir(CMD_TV_CH_PREV)));
        findViewById(R.id.sbvoldown).setOnClickListener(new ClickListener(hex2ir(CMD_SB_VOLDOWN)));
        findViewById(R.id.sbvolup).setOnClickListener(new ClickListener(hex2ir(CMD_SB_VOLUP)));
        findViewById(R.id.tvsource).setOnClickListener(new ClickListener(hex2ir(CMD_SB_SOURCE)));



    }

    // based on code from http://stackoverflow.com/users/1679571/randy (http://stackoverflow.com/a/25518468)
    private IRCommand hex2ir(final String irData) {
        List<String> list = new ArrayList<String>(Arrays.asList(irData.split(" ")));

        list.remove(0); // dummy
        int frequency = Integer.parseInt(list.remove(0), 16); // frequency
        list.remove(0); // seq1
        list.remove(0); // seq2

        frequency = (int) (1000000 / (frequency * 0.241246));
        int pulses = 1000000 / frequency;
        int count;
        Log.d("Remote", list+"pulses01: " + pulses);
        int[] pattern = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            count = Integer.parseInt(list.get(i), 16);
            pattern[i] = count * pulses;
        }
        Log.d("Remote", frequency+"frequency02: " + pattern);
        return new IRCommand(frequency, pattern);
    }

    private class ClickListener implements View.OnClickListener {
        private final IRCommand cmd;

        public ClickListener(final IRCommand cmd) {
            this.cmd = cmd;
        }

        @Override
        public void onClick(final View view) {
            android.util.Log.d("Remote", "frequency: " + cmd.freq);
            android.util.Log.d("Remote", "pattern: " + Arrays.toString(cmd.pattern));
            irManager.transmit(cmd.freq, cmd.pattern);
        }
    }

    private class IRCommand {
        private final int freq;
        private final int[] pattern;

        private IRCommand(int freq, int[] pattern) {
            this.freq = freq;
            this.pattern = pattern;
        }
    }
}
