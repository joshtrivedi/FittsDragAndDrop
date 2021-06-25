package ca.yorku.cse.mack.fittsdraganddrop;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * <h1>FittsDragAndDrop</h1>
 *
 * <h3>Summary</h3>
 *
 * <ul> <li> Android experiment software for evaluating touch-based or stylus-based drag-and-drop actions using Fitts'
 * law. <p>
 *
 * <li>Implements both the two-dimensional (2D) and one-dimensional (1D) tasks described in ISO 9241-9 (updated in 2012
 * as ISO/TC 9241-411). <p>
 *
 * <li>Includes options to tailor the app to children.  In this case, the targets are animals and the object to drag is
 * an apple which is "fed" to the animals. <p>
 *
 * <li>User performance data gathered and saved in output files for follow-up analyses. <p> </ul>
 *
 * <h3>Related References</h3>
 *
 * The following publications present research where this software was used: <p>
 *
 * <ul>
 *
 * <li>(a paper is coming soon; stay tuned) <p>
 *
 * </ul>
 *
 * The following publications provide background information on Fitts' law and experimental testing using the Fitts'
 * paradigm. <p>
 *
 * <ul> <li><a href="http://www.yorku.ca/mack/hhci2018.html">"Fitts' law"</a>, by MacKenzie (Wiley <i>Handbook of
 * Human-Computer Interaction 2018</i>). <p>
 *
 * <li><a href="http://www.yorku.ca/mack/ijhcs2004.pdf">"Towards a Standard for Pointing Device Evaluation: Perspectives
 * on 27 Years of Fitts' Law Research in HCI"</a>, by Soukoreff and MacKenzie (<i>IJHCS 2004</i>). <p>
 *
 * <li><a href="http://www.yorku.ca/mack/HCI.html">"Fitts' Law as a Research and Design Tool in Human-Computer
 * Interaction"</a>, by MacKenzie (<i>HCI 1992</i>). <p> </ul> <p>
 *
 * <h3>Setup Parameters</h3>
 *
 * Upon launching, the program presents a setup dialog: <p> <center><a href="FittsDragAndDrop-1.jpg"><img
 * src="FittsDragAndDrop-1.jpg" width="200"></a></center> <p> </center>
 *
 * The parameters are embedded in the application. The default settings (shown) may be changed by selecting the
 * corresponding spinner. Changes may be saved. Saved changes become the default settings when the application is next
 * launched. <p>
 *
 * The setup parameters are as follows: <p>
 *
 * <blockquote> <table border="1" cellspacing="0" cellpadding="6"> <tr bgcolor="#cccccc"> <th>Parameter <th>Description
 *
 * <tr> <td valign="top">Participant code <td>Identifies the current participant. <p>
 *
 * <tr> <td valign="top">Session code <td>Identifies the session. This code is useful if testing proceeds over multiple
 * sessions to gauge the progression of learning. <p>
 *
 * <tr> <td valign="top">Block code (auto) <td>Identifies the block of testing. This code is generated automatically.
 * The first block of testing is "B01", then "B02", and so on. Output data files include the block code in the filename.
 * The first available block code is used in opening data files for output. This prevents overwriting data from an
 * earlier block of testing. <p>
 *
 * <tr> <td valign="top">Group code <td>Identifies the group to which the participant was assigned. This code is needed
 * if counterbalancing was used (i.e., participants were assigned to groups to offset order effects). This is common
 * practice for testing the levels of a within-subjects independent variable. <p>
 *
 * <tr> <td valign="top">Condition code <td>An arbitrary code to associate a test condition with a block of trials. This
 * parameter might be useful if the user study includes conditions that are not inherently part of the application
 * (e.g., Gender &rarr; male, female; User stance &rarr; sitting, standing, walking). <p>
 *
 * <tr> <td valign="top">Mode <td>Set to either "1D" or "2D" to control whether the task is one-dimensional or
 * two-dimensional. <p>
 *
 * <tr> <td colspan=2 valign=center> NOTE: The setup parameters above appear in the filename for the output data files
 * (e.g., <code>FittsDragAndDrop-P01-S01-B01-G01-C01-1D.sd1</code>). They also appear as data columns in the output data
 * files.
 *
 * <tr> <td valign="top">Number of trials (1D) <td>Specifies the number of back-and-forth selections in a block of
 * trials. This setup parameter is only relevant if Mode = 1D. <p>
 *
 * <tr> <td valign="top">Number of targets (2D) <td>Specifies the number of targets that appear in the layout circle.
 * This setup parameter is only relevant if Mode = 2D. <p>
 *
 * <tr> <td valign="top">Target amplitude (A) <td>Specifies either the diameter of the layout circle (2D) or the
 * center-to-center distance between targets (1D). The spinner offers several choices (but see note 2 below). <p>
 *
 * <tr> <td valign="top">Target width (W) <td>Specifies the width of targets. This is either the diameter of the target
 * circles (2D) or the width of the rectangles (1D). The spinner offers several choices (but see note 2 below). <p>
 *
 * Notes:<br> 1. The total number of <i>A-W</i> conditions (sequences) in a block is <i>n &times; m</i>, where <i>n</i>
 * is the number of target amplitudes and <i>m</i> is the number of target widths.<br> 2. The <i>A-W</i> values are
 * scaled such that the widest condition (largest A, largest W) spans the device's display with minus 1/16 inch on each
 * side. <p>
 *
 * <tr> <td valign="top">Vibrotactile feedback <td>A checkbox parameter. If checked, a 10 ms vibrotactile pulse is
 * emitted if a target is selected in error (i.e., the finger-up action is outside the target). <p>
 *
 * <tr> <td valign="top">Audio feedback <td>A checkbox parameter. If checked, an auditory beep is heard if a target is
 * selected in error (i.e., the finger-up action is outside the target). <p>
 *
 * <tr> <td valign="top">Speech feedback <td>A checkbox parameter. This option is for use with children.  See "Using
 * FittsDragAndDrop With Children" below. <p>
 *
 * <tr> <td valign="top">FittsFarm style <td>A checkbox parameter. This option is for use with children.  See "Using
 * FittsDragAndDrop With Children" below. <p>
 *
 * <tr> <td valign="top">Show all targets <td>A checkbox parameter. If checked, all targets appear.  This is the default
 * option.  See "Operation", below. If unchecked, only the drag object and the target appear, as shown below. <p>
 *
 * <center> <a href="FittsDragAndDrop-2.jpg"><img src="FittsDragAndDrop-2.jpg" height=200></a> </center> <p>
 *
 *
 * </table> </blockquote>
 *
 * <h3>Operation</h3>
 *
 * Once the setup parameters are chosen, the testing begins by tapping "OK". The first screen to appear is a transition
 * screen to ensure the participant is ready (below, left). Once the blue circle is tapped, the first test condition
 * appears. Examples are shown below for the 2D task (below, center) and the 1D task (below, right). <p>
 *
 * <center> <a href="FittsDragAndDrop-3.jpg"><img src="FittsDragAndDrop-3.jpg" height=300></a>&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp; <a href="FittsDragAndDrop-4.jpg"><img src="FittsDragAndDrop-4.jpg"
 * height=300></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="FittsDragAndDrop-5.jpg"><img src="FittsDragAndDrop-5.jpg"
 * height=300></a> </center> <p>
 *
 * The participant selects targets using drag-and-drop operations.  Each trial requires a drag operation between a
 * "from" target and a "to" target.  The drag object appears in the "from" target as a black circle, which is either 1/4
 * inch wide or the width of the smallest target in the block, whichever is less.  The drag object is acquired by a
 * finger-down or stylus-down action.  A down-action anywhere within the "from" target circle is sufficient to acquire
 * the drag object.  When acquired, the drag object snaps to the location of the finger-down or stylus-down action.  The
 * drag object is then dragged to the "to" target and dropped by a finger-up or stylus-up action.  A "tick" audio sound
 * is heard on finger-up or stylus-up (if audio feedback is enabled). <p>
 *
 * While dragging, a gradient heat map appears at the location of finger or stylus contact on the display.  This
 * provides additional visual feedback for the dragging operation.  See below. <p>
 *
 * <center> <a href="FittsDragAndDrop-9.jpg"><img src="FittsDragAndDrop-9.jpg" height=300></a> </center> <p>
 *
 * For the 2D mode, drag-and-drops proceed in a pattern moving around the layout circle.  Each "from" target is the last
 * "to" target.  Each new "to" target is beside the last "from" target, so as to proceed around the layout circle.  For
 * the 1D mode, drag-and-drops proceed back and forth until the specified number of selections have occurred. <p>
 *
 * Timing for a sequence begins on the first up-action (on the first target) and ends on the last up-action (on the last
 * target).  Bear in mind that participants should <i>not</i> pause between drag-and-drop operations, because the time
 * for each trial begins on the finger-up or stylus-up action that ended the previous trial.  This is consistent with
 * the "serial tapping task" paradigm used in Fitts' law experiments. <p>
 *
 * Each trial must begin correctly, which is to say, the finger- or stylus-down action must be within the "from" target
 * where
 * the
 * drag object appears. Errors at the end of trials are permitted, however. If the finger- or stylus-up action is
 * outside the "to"
 * target, the trial ends, an error is logged, and an auditory beep and vibrotactile pulse are emitted (if the
 * corresponding options are selected).  The only requirement is that for each trial the movement distance in the
 * direction of the "to" target must be at least half the required movement distance.  This is implemented to avoid
 * severe outliers, which occur, for example, if the participant "drops" a target immediately upon acquiring it or
 * perhaps inadvertently does a double-selection.  If a outlier trial is detected, a popup dialog appears and the
 * sequence must be restarted.  The number of such events appears as a "sequence repeat count" in the sd2 output data
 * file. <p>
 *
 * A series of trials for a single <i>A-W</i> condition is called a "sequence". At the end of each sequence, results
 * appear on the display. See below: <p>
 *
 * <center><a href="FittsDragAndDrop-6.jpg"><img src="FittsDragAndDrop-6.jpg" height="400"></a></center> <p>
 *
 * Once all the <i>A-W</i> conditions in a block are finished, user performance data are saved in files and the
 * application returns to the setup dialog. The data files are located in the device's public storage directory in a
 * folder named <code>FittsDragAndDropData</code>. <p>
 *
 * <h3>Using FittsDragAndDrop With Children</h3>
 *
 * The original inspiration for FittsDragAndDrop was a Flash application called Fitts Farm, developed by <a
 * href="http://www.uclan.ac.uk/staff_profiles/dr_brendan_cassidy.php">Brendan Cassidy</a> and <a
 * href="http://www.uclan.ac.uk/staff_profiles/professor_janet_read.php">Janet Read</a> of the <a
 * href="http://www.uclan.ac.uk/research/explore/groups/child_computer_interaction_group_chici.php">ChiCi</a> research
 * group at the University of Central Lancashire in the UK.  To engage children in mobile interaction and, in
 * particular, drag-and-drop interactions, the Fitts Farm application was designed with a game-like appeal.  Children
 * are asked to feed farm animals. This is done by dragging graphical apples on to graphical animals and dropping the
 * apple on the image of the animal. <p>
 *
 * FittsDragAndDrag has two setup options to configure the interaction to suit research with children.  If the "Fitts
 * Farm style" checkbox option is selected, the introductory screen announces that there are animals in need of feeding
 * (below, left).  Another option, "Speech feedback", adds (annoying!) speech feedback about the animals being hungry,
 * etc. <p>
 *
 * <center><a href="FittsDragAndDrop-7.jpg"><img src="FittsDragAndDrop-7.jpg" height="400"></a> &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="FittsDragAndDrop-8.jpg"><img src="FittsDragAndDrop-8.jpg" height="400"></a> </center> <p>
 *
 * In the style of Fitts Farm, an apple is used as the drag object and a graphic image of an animal appears in the
 * target circle (above, right). The animal image is resized to fill the target circle.  The application includes about
 * 15 animal images:
 *
 * <center><a href="FittsDragAndDrop-11.jpg"><img src="FittsDragAndDrop-11.jpg" height="180"></a> </center> <p>
 *
 * The target image changes randomly from trial to trial. <p>
 *
 * <h3>Output Data Files</h3>
 *
 * For each block of testing, three output data files are created: sd1,  sd2, and sd3. ("sd" is for "summary data".) The
 * data are comma delimited for easy importing into a spreadsheet or statistics program. <p>
 *
 * <h4>sd1 Output File</h4>
 *
 * The sd1 file contains one line per trial.  The columns contain the following data: <p>
 *
 * <pre>
 *      Participant - participant code
 *      Session - session code
 *      Block - block code
 *      Group - group code
 *      Condition - condition code
 *      Mode - mode code (1D or 2D)
 *      Trial - trial number
 *      A - target amplitude
 *      W - target width
 *      FromX - x coordinate of center of from-target
 *      FromY - y coordinate of center of from-target
 *      TargetX - x coordinate of center of to-target
 *      TargetY - y coordinate of center of to-target
 *      FingerDownX - x coordinate of finger-down event at end of trial
 *      FingerDownY - y coordinate of finger-down event at end of trial
 *      SelectX - x coordinate of finger-up event at end of trial (target selection)
 *      SelectY - y coordinate of finger-up event at end of trial (target selection)
 *      xDelta - (see below)
 *      FingerDownUpDelta - Pythagorean distance between finger-down and finger-up events
 *      FingerDownUpTime - time in ms between finger-down and finger-up events
 *      DistanceFromTargetCenter - Pythagorean distance from selection coordinate to target center
 *      PickupMisses - number of times the drag object was missed at the beginning of trial
 *      Error - 0 = target selected, 1 = target missed
 *      MT - movement time in ms for the trial
 * </pre>
 *
 * Note: All sizes, distances, and coordinates are in pixel units for the test device. <p>
 *
 * <code>xDelta</code> is the <i>x</i>-distance from the finger-up coordinate to the center of the target. It is
 * normalized relative to the center of the target and to the task axis. For example, <code>xDelta</code> = 1 is the
 * equivalent of a one-pixel overshoot while <code>xDelta</code> = &minus;1 is the equivalent of a one-pixel undershoot.
 * Note that <code>xDelta</code> = 0 does not mean selection was precisely at the centre of the target. It means the
 * selection was on a line orthogonal to the task axis going through the centre of the target. This is consistent with
 * the inherently one-dimensional nature of Fitts' law. <p>
 *
 * <code>xDelta</code> is important for calculating Fitts' throughput. The standard deviation in the <code>xDelta</code>
 * values collected over a sequence of trials is <i>SD</i><sub>x</sub>. This is used in the calculation of throughput
 * (<i>TP</i>) as follows: <p>
 *
 * <blockquote> <i>W</i><sub>e</sub> = 4.133 &times; <i>SD</i><sub>x</sub> <p>
 *
 * <i>ID</i><sub>e</sub> = log<sub>2</sub>(<i>A</i><sub>e</sub> / <i>W</i><sub>e</sub> + 1) <p>
 *
 * <i>TP</i> = <i>ID</i><sub>e</sub> / <i>MT</i> <p> </blockquote>
 *
 * <h4>sd2 Output File</h4>
 *
 * The sd2 file contains one line per sequence.   The columns contain the following data: <p>
 *
 * <pre>
 *      Participant - participant code
 *      Session - session code
 *      Block - block code
 *      Group - group code
 *      Condition - condition code
 *      Mode - mode code (1D or 2D)
 *      Trials - number of trials in the sequence
 *      A - specified target amplitude
 *      W - specified target width
 *      ID - specified index of difficulty
 *      Ae - actual or effective movement amplitude (*)
 *      We - actual or effective target width (*)
 *      IDe - actual or effective index of difficulty (*)
 *      PickupMisses - number of times the drag object was missed at the beginning of a trial (*)
 *      SequenceRepeatCount - number of times the sequence was repeated (see below) (*)
 *      MT - mean movement time in ms over all trials in the sequence (*)
 *      ErrorRate - error rate (%) (*)
 *      TP - Fitts' throughput in bits per second (*)
 * </pre>
 *
 * The entries above with asterisks (*) are user performance measures. These reflect how the user actually performed
 * while doing the sequence of trials. The measures most commonly used as dependent variables in Fitts' law experiments
 * are the last three: movement time, error rate, and throughput. <p>
 *
 * The <code>SequenceRepeatCount</code> is used in conjunction with the outlier criterion described above. No data are
 * saved for an outlier sequence. However, the <code>SequenceRepeatCount</code> entry in the sd2 file indicates the
 * number of times the sequence was repeated due to the outlier criterion. Usually, <code>SequenceRepeatCount</code> = 0
 * (hopefully!). <p>
 *
 * <h4>sd3 Output Data</h4>
 *
 * The sd3 file contains trace data for the path of finger or stylus movement while dragging. For each trial, the
 * on-going timestamps, <i>x</i> coordinates, and <i>y</i> coordinates are collected and saved. A separate desktop
 * utility, <a href="file:///C:/Users/mack/Desktop/Scott-new/Java/FITTS_LAW/doc/FittsTrace.html">FittsTrace</a>,
 * facilitates viewing the trace data. <p>
 *
 * The following are examples of "sd" (summary data) files: <p>
 *
 * <ul> <li><a href="FittsDragAndDrop-sd1-example.txt">sd1 example</a>
 * <li><a href="FittsDragAndDrop-sd2-example.txt">sd2
 * example</a> <li><a href="FittsDragAndDrop-sd3-example.txt">sd3 example</a> </ul> <p>
 *
 * Actual output files use "FittsDragAndDrop" as the base filename. This is followed by the participant code, the
 * session code, the block code, the group code, the condition code, and the mode, for example,
 * <code>FittsDragAndDrop-P01-S01-B01-G01-C01-1D.sd1</code>. <p>
 *
 * In most cases, the sd2 data files are the primary files used for data analyses in an experimental evaluation. The
 * data in the sd2 files are full-precision, comma-delimited, to facilitate importing into a spreadsheet or statistics
 * application. Below is an example for the sd2 file above, after importing into Microsoft <i>Excel</i>: (click to
 * enlarge) <p>
 *
 * <center> <a href="FittsDragAndDrop-10.jpg"><img src="FittsDragAndDrop-10.jpg" width=1000></a> </center> <p>
 *
 * When using this application in an experiment, it is a good idea to terminate all other applications and to disable
 * the system's WiFi and Bluetooth transceivers. This will maintain the integrity of the data collected and ensure that
 * the application runs without hesitations. <p>
 *
 * @author (c) Scott MacKenzie, 2018
 */

public class FittsDragAndDropActivity extends Activity implements View.OnTouchListener, MediaPlayer.OnCompletionListener
{
    final String MYDEBUG = "MYDEBUG";
    final String DATA_DIRECTORY = "/FittsDragAndDropData/";
    final String APP = "FittsDragAndDrop";

    final String SD1_HEADER = "Participant,Session,Block,Group,Condition,Mode,Trial,A,W,FromX,FromY,TargetX,TargetY," +
            "FingerDownX,FingerDownY,SelectX,SelectY,xDelta,FingerDownUpDelta,FingerDownUpTime(ms)," +
            "DistanceFromTargetCenter,PickupMisses,Error,MT(ms)" + System.getProperty("line.separator");
    final String SD2_HEADER = "App,Participant,Session,Block,Group,Condition,Mode,Trials," +
            "A,W,ID,Ae,We,IDe,PickupMisses,SequenceRepeatCount,MT(ms),ErrorRate(%),TP(bps)" + System.getProperty
            ("line.separator");
    final String SD3_HEADER = "TRACE DATA" +
            System.getProperty("line.separator") +
            "App,Participant,Condition,Block,Sequence,A,W,Trial,from_x,from_y,to_x,to_y,{t_x_y}" +
            System.getProperty("line.separator");
    final float TWO_TIMES_PI = 6.283185307f;
    final int VIBRATION_PULSE_DURATION = 10;

    ExperimentPanel expPanel;
    String participantCode, sessionCode, blockCode, groupCode, conditionCode;
    String dimensionMode;
    boolean vibrotactileFeedback, auditoryFeedback, speechFeedback;
    boolean fittsFarmStyle, showAllTargets;
    int numberOfTrials, numberOfTargets, outlierSequenceCount, pickupMissCountSequence, pickupMissCountTrial;
    float[] amplitude, width;
    BufferedWriter sd1, sd2, sd3;
    File f1, f2, f3;
    int screenOrientation, targetHeight1D;
    String sd2Header;

    AmplitudeWidth[] aw; // task conditions (A-W pairs)
    float xCenter, yCenter;
    float xFingerDown, yFingerDown;
    long fingerDownTime, now, sequenceStartTime;
    long trialStartTime, trialTime;
    boolean sequenceStarted, waitTargetSelected, outlier;
    boolean dragInProgress;
    int awIdx, selectionCount, trialError;
    Vibrator vib;
    MediaPlayer missSound, tickSound, thankYou, stillHungry, takeANap, animalsAreHungry;
    StringBuilder sb1, sb2, sb3, results;

    // new stuff to streamline calculation of Throughput
    PointF[] from;
    PointF[] to;
    PointF[] select;
    float[] mtArray;
    // -----

    ArrayList<TracePoint> tracePoint;
    String sd3Leadin;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // init study parameters
        Bundle b = getIntent().getExtras();
        participantCode = b.getString("participantCode");
        sessionCode = b.getString("sessionCode");
        blockCode = "B01"; // always start here
        groupCode = b.getString("groupCode");
        conditionCode = b.getString("conditionCode");
        dimensionMode = b.getString("mode");
        numberOfTrials = b.getInt("numberOfTrials");
        numberOfTargets = b.getInt("numberOfTargets");
        amplitude = getValues(b.getString("amplitude"));
        width = getValues(b.getString("width"));
        vibrotactileFeedback = b.getBoolean("vibrotactileFeedback");
        auditoryFeedback = b.getBoolean("auditoryFeedback");
        speechFeedback = b.getBoolean("speechFeedback");
        fittsFarmStyle = b.getBoolean("fittsFarmStyle");
        showAllTargets = b.getBoolean("showAllTargets");
        screenOrientation = b.getInt("screenOrientation");

        // force the UI to operate in the device's default orientation
        if (getDefaultOrientation() == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // ===================
        // File initialization
        // ===================

        // make a working directory (if necessary) to store data files
        File dataDirectory = new File(Environment.getExternalStorageDirectory() +
                DATA_DIRECTORY);
        if (!dataDirectory.exists() && !dataDirectory.mkdirs())
        {
            Log.e(MYDEBUG, "ERROR --> FAILED TO CREATE DIRECTORY: " + DATA_DIRECTORY);
            super.onDestroy(); // cleanup
            this.finish(); // terminate
        }

        /*
         * The following do-loop creates data files for output and a string sd2Header to write to the sd2
         * output files.  Both the filenames and the sd2Header are constructed by combining the setup parameters
         * so that the filenames and sd2Header are unique and also reveal the conditions used for the block of input.
         *
         * The block code begins "B01" and is incremented on each loop iteration until an available
         * filename is found.  The goal, of course, is to ensure data files are not inadvertently overwritten.
         */
        int blockNumber = 0;
        do
        {
            ++blockNumber;
            String blockCode = String.format(Locale.CANADA, "B%02d", blockNumber);
            String baseFilename = String.format("%s-%s-%s-%s-%s-%s-%s", APP, participantCode,
                    sessionCode, blockCode, groupCode, conditionCode, dimensionMode);

            f1 = new File(dataDirectory, baseFilename + ".sd1");
            f2 = new File(dataDirectory, baseFilename + ".sd2");
            f3 = new File(dataDirectory, baseFilename + ".sd3");

            // also make a comma-delimited leader that will begin each data line written to the sd2 file
            sd2Header = String.format("%s,%s,%s,%s,%s,%s,%s", APP, participantCode, sessionCode,
                    blockCode, groupCode, conditionCode, dimensionMode);
        } while (f1.exists() || f2.exists());

        try
        {
            sd1 = new BufferedWriter(new FileWriter(f1));
            sd2 = new BufferedWriter(new FileWriter(f2));
            sd3 = new BufferedWriter(new FileWriter(f3));

            // output header in sd1 file
            sd1.write(SD1_HEADER, 0, SD1_HEADER.length());
            sd1.flush();

            // output header in sd2 file
            sd2.write(SD2_HEADER, 0, SD2_HEADER.length());
            sd2.flush();

            // output header in sd3 file
            sd3.write(SD3_HEADER, 0, SD3_HEADER.length());
            sd3.flush();

        } catch (IOException e)
        {
            Log.e(MYDEBUG, "ERROR OPENING DATA FILES! e=" + e.toString());
            super.onDestroy();
            this.finish();

        } // end file initialization

        // determine screen width and height
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        xCenter = screenWidth / 2f;
        yCenter = screenHeight / 2f;
        targetHeight1D = (int)(screenHeight * 0.9f); // 1D targets are 90% of screen height

        // initialize the experiment panel
        expPanel = (ExperimentPanel)findViewById(R.id.experimentpanel);
        expPanel.setOnTouchListener(this);
        expPanel.panelWidth = screenWidth;
        expPanel.panelHeight = screenHeight;
        expPanel.mode = dimensionMode;
        expPanel.waitStartCircleSelect = true;
        expPanel.nextRandomGraphic(); // needed before 1st trial
        expPanel.fittsFarmStyle = fittsFarmStyle;
        expPanel.showAllTargets = showAllTargets;
        expPanel.showBanner = true;

        // scale target amplitudes and width to use all the available screen space.  This involves...

        // ...first, finding the largest unscaled amplitude, then...
        float largestAmplitude = 0;
        for (float value : amplitude)
            if (value > largestAmplitude)
                largestAmplitude = value;

        // ...finding the largest unscaled target.
        float largestWidth = 0;
        for (float value : width)
            if (value > largestWidth)
                largestWidth = value;

        /*
         * Now determine a scaling factor such that the largest amplitude and widest target will span the available
         * display width or display height (whichever is less) minus 1/16 inch
         */
        float scaleFactor = Math.min(screenWidth, screenHeight) / (largestAmplitude + largestWidth + 10.0f * dm
                .density);

        // scale amplitudes
        for (int i = 0; i < amplitude.length; ++i)
            amplitude[i] *= scaleFactor;

        // scale widths
        for (int i = 0; i < width.length; ++i)
            width[i] *= scaleFactor;

        // find smallest scaled target width (in pixels)
        float smallestWidth = Float.MAX_VALUE;
        for (float value : width)
            if (value < smallestWidth)
                smallestWidth = value;
        expPanel.setDragObjectWidth((int)smallestWidth);

        // prepare vibrotactile feedback (only used for misses and contingent on setup option)
        vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        // prepare sound clips as MediaPlayer objects (tweak volumes, as necessary)
        missSound = MediaPlayer.create(this, R.raw.miss);
        missSound.setVolume(.1f, .1f);
        tickSound = MediaPlayer.create(this, R.raw.tick2);
        tickSound.setVolume(.2f, .2f);
        thankYou = MediaPlayer.create(this, R.raw.thankyou);
        thankYou.setVolume(.4f, .4f);
        thankYou.setOnCompletionListener(this); // see below (onCompletion)
        stillHungry = MediaPlayer.create(this, R.raw.stillhungry);
        stillHungry.setVolume(.4f, .4f);
        takeANap = MediaPlayer.create(this, R.raw.nap);
        takeANap.setVolume(.4f, .4f);
        animalsAreHungry = MediaPlayer.create(this, R.raw.animalsarehungry);
        animalsAreHungry.setVolume(.4f, .4f);

        // tweaks needed to accommodate 1D vs. 2D modes
        if (dimensionMode.equals("1D"))
            numberOfTargets = 2; // reciprocal tapping
        if (dimensionMode.equals("2D"))
            numberOfTrials = numberOfTargets;

        // arrays needed for Throughput calculation (done on a per-sequence basis)
        from = new PointF[numberOfTrials];
        to = new PointF[numberOfTrials];
        select = new PointF[numberOfTrials];
        mtArray = new float[numberOfTrials];
        aw = getAmplitudeWidthArray(amplitude, width);
        awIdx = 0;
        waitTargetSelected = true;

        sequenceStarted = false;
        dragInProgress = false;

         /*
         * We want to play the banner sound now (contingent on setup options), but this isn't possible because the
         * MediaPlayer is still loading the clip (see above).  So, we use a delay timer.
         */
        CountDownTimer delayTimer = new CountDownTimer(300, 300)
        {
            public void onTick(long millisUntilFinished)
            {
            }

            public void onFinish()
            {
                if (fittsFarmStyle && speechFeedback)
                {
                    animalsAreHungry.start();
                }
            }
        };
        delayTimer.start();

    } // end onCreate

    @Override
    public void onStop()
    {
        super.onStop();
        // as per https://developer.android.com/reference/android/media/MediaPlayer.html
        missSound.release();
        tickSound.release();
        thankYou.release();
        stillHungry.release();
        takeANap.release();
        animalsAreHungry.release();
    }

    // called when the thankYou speech segment finishes playing
    @Override
    public void onCompletion(MediaPlayer mp)
    {
        if (expPanel.done)
            takeANap.start();
        else
            stillHungry.start();
    }

    // convert the amplitude/width string in the spinners to float array
    private float[] getValues(String valuesArg)
    {
        StringTokenizer st = new StringTokenizer(valuesArg, ", ");
        int i = 0;
        float[] values = new float[st.countTokens()];
        while (st.hasMoreTokens())
            values[i++] = Float.parseFloat(st.nextToken());
        return values;
    }

    @Override
    public boolean onTouch(View v, MotionEvent me)
    {
        float x = me.getX();
        float y = me.getY();
        now = me.getEventTime();

        expPanel.xTouchPoint = x;
        expPanel.yTouchPoint = y;

        if (me.getAction() == MotionEvent.ACTION_DOWN) // ==================================================
        {
            // this flag enables the gradient heat map to be drawn under the user's finger
            expPanel.fingerDown = true;

            if (!expPanel.waitStartCircleSelect)
            {
                if (expPanel.fromTarget.r.contains(x, y))
                    doDragObjectBegin(x, y); // object correctly acquired
                else if (sequenceStarted)
                {
                    ++pickupMissCountTrial;
                    ++pickupMissCountSequence; // object missed
                }
            }

        } else if (me.getAction() == MotionEvent.ACTION_MOVE) // ===========================================
        {
            if (!expPanel.waitStartCircleSelect && dragInProgress)
            {
                doDragObject(x, y);
            }

        } else if (me.getAction() == MotionEvent.ACTION_UP) // =============================================
        {
            // disable drawing of gradient heat map under user's finger
            expPanel.fingerDown = false;

            if (expPanel.waitStartCircleSelect)
            {
                if (expPanel.startCircle.inTarget(x, y))
                {
                    doStartCircleSelected();
                }

            } else if (dragInProgress)
            {
                doTargetSelected(x, y);
            }
        }
        return true;
    }

    private void configureTargets(int awIdx)
    {
        for (int i = 0; i < numberOfTargets; ++i)
        {
            float x = xCenter + (aw[awIdx].a / 2f) * (float)Math.cos(TWO_TIMES_PI * ((float)i / numberOfTargets));
            float y = yCenter + (aw[awIdx].a / 2f) * (float)Math.sin(TWO_TIMES_PI * ((float)i / numberOfTargets));
            if (expPanel.mode.equals("1D"))
                expPanel.targetSet[i] = new Target(Target.RECTANGLE, x, y, aw[awIdx].w, targetHeight1D, Target
                        .NORMAL);
            else
                expPanel.targetSet[i] = new Target(Target.CIRCLE, x, y, aw[awIdx].w, aw[awIdx].w, Target.NORMAL);
        }
        // Don't set target to select yet. This is done when start circle is selected.
    }

    private AmplitudeWidth[] getAmplitudeWidthArray(float[] aArray, float[] wArray)
    {
        AmplitudeWidth[] aw = new AmplitudeWidth[aArray.length * wArray.length];
        for (int i = 0; i < aw.length; ++i)
            aw[i] = new AmplitudeWidth(aArray[i / wArray.length], wArray[i % wArray.length]);

        // shuffle
        Random r = new Random();
        for (int i = 0; i < aw.length; ++i)
        {
            int idx = r.nextInt(aw.length);
            AmplitudeWidth temp = aw[idx];
            aw[idx] = aw[i];
            aw[i] = temp;
        }
        return aw;
    }

    // grab the timestamp and x-y coordinate for a finger down event
    public void doDragObjectBegin(float xArg, float yArg)
    {
        dragInProgress = true;

        xFingerDown = xArg;
        yFingerDown = yArg;
        expPanel.xTouchPoint = xArg;
        expPanel.yTouchPoint = yArg;

        if (sequenceStarted)
        {
            fingerDownTime = now;
            //tracePoint = new ArrayList<>();
            // this is the 2nd trace point in the trial (beginning of drag operation)
            tracePoint.add(new TracePoint(now - trialStartTime, (int)xArg, (int)yArg));
        }
    }

    public void doDragObject(float xArg, float yArg)
    {
        expPanel.xDragObject = xArg;
        expPanel.yDragObject = yArg;

        if (sequenceStarted)
        {
            tracePoint.add(new TracePoint(now - trialStartTime, (int)xArg, (int)yArg));
        }
    }

    public void doStartCircleSelected()
    {
        if (expPanel.done) // start circle displayed after last sequence, select to finish
            doEndBlock();

        expPanel.waitStartCircleSelect = false;
        if (awIdx < aw.length)
        {
            expPanel.targetSet = new Target[numberOfTargets];
            configureTargets(awIdx);
        }

        expPanel.toTarget = expPanel.targetSet[0];
        expPanel.toTarget.status = Target.TARGET; // target to select
        expPanel.fromTarget = expPanel.targetSet[expPanel.targetSet.length / 2];

        // set starting position of drag object
        expPanel.xDragObject = expPanel.fromTarget.xCenter;
        expPanel.yDragObject = expPanel.fromTarget.yCenter;
        selectionCount = 0;
    }

    // Done! close data files and exit
    private void doEndBlock()
    {
        try
        {
            sd1.close();
            sd2.close();
            sd3.close();

            /*
             * Make the saved data files visible in Windows Explorer. There seems to be bug doing
             * this with Android 4.4. I'm using the following code, instead of sendBroadcast.
             * See...
             *
             * http://code.google.com/p/android/issues/detail?id=38282
             *
             * ... for four years of chitter-chatter on this lingering issue.  Wow!
             *
             * Also, for the mime types, I'm using an array that explicitly identifies the .sd1, .sd2, and .sd3 files
             * as text files.  This seems to necessary, lest Android decides these files are of some other file type.
             * See...
             *
             * https://stackoverflow.com/questions/14492138/mime-type-for-txt-files
             */
            MediaScannerConnection.scanFile(this, new String[] {f1.getAbsolutePath(), f2.getAbsolutePath(),
                    f3.getAbsolutePath()}, new String[] {"text/plain", "text/plain", "text/plain"}, null);
        } catch (IOException e)
        {
            Toast.makeText(this, String.format("ERROR WRITING TO DATA FILE: e = %s", e), Toast.LENGTH_LONG).show();
        }
        this.finish();
    }

    /**
     * Process a target selection.  There is the possibility that the target selection is the first selection in a
     * sequence.  In that case, data are not saved because timing for a sequence begins on finger-up (i.e., at the end
     * of the first selection).  Most of the target selections will be for targets in a sequence.  In these cases, data
     * are saved.  We also need to deal with the possibility of an error, the sort of feedback to give, etc.
     */
    void doTargetSelected(float xSelect, float ySelect)
    {
        // we've got a finger-up so clear this flag
        dragInProgress = false;

        // hit or miss? (respond appropriately)
        trialError = expPanel.toTarget.inTarget(xSelect, ySelect) ? 0 : 1;

        // perhaps start a sequence (if the user correctly selects the 1st target)
        if (!sequenceStarted)
        {
            if (trialError == 1) // failed start of sequence, try again
            {
                expPanel.resetDragObject();

            } else // correct start of sequence; do some preparatory things
            {
                sequenceStarted = true;
                sequenceStartTime = now;
                trialStartTime = now; // need this at the beginning of a sequence

                // new...
                tracePoint = new ArrayList<>();

                // this the first trace point in the sequence
                tracePoint.add(new TracePoint(0, (int)xSelect, (int)ySelect));

                advanceTarget();
                expPanel.fromTarget = expPanel.targetSet[0];
                sb1 = new StringBuilder();
                sb2 = new StringBuilder();
                results = new StringBuilder();
                if (auditoryFeedback)
                    tickSound.start();
            }

            // we're not saving any data yet, so return now
            return;
        }

        if (trialError == 1)
        {
            // provide feedback (as per setup) if the user misses the target
            if (vibrotactileFeedback)
                vib.vibrate(VIBRATION_PULSE_DURATION);
            if (auditoryFeedback)
                missSound.start();

        } else // provide feedback (as per setup) if the user correctly selected the target
        {
            if (auditoryFeedback)
                tickSound.start();
        }

        // use static methods in the Throughput class to retrieve dx and ae, as per the usual calculations
        float xDelta = Throughput.getTrialDeltaX(expPanel.fromTarget.targetCenter(), expPanel.toTarget.targetCenter(),
                new PointF(xSelect, ySelect));
        float ae = Throughput.getTrialAe(expPanel.fromTarget.targetCenter(), expPanel.toTarget.targetCenter(),
                new PointF(xSelect, ySelect));

        // compute distance from select point to target center (this is "b" in the usual calculations)
        float b = (float)Math.hypot(xSelect - expPanel.toTarget.xCenter, ySelect - expPanel.toTarget.yCenter);

		/*
         * DEFINITION OF OUTLIER: Any trial where the actual distance moved is less the 1/2 the
		 * specified amplitude is deemed an outlier. If an outlier occurs, the sequence must be repeated.
		 */

        if (ae < aw[awIdx].a / 2f)
        {
            outlier = true;
            doEndSequence();
            return;
        }

        // trial time is from last finger-up to current finger-up
        trialTime = now - trialStartTime;
        //trialStartTime = now;

        String fingerDownUpTime = String.format(Locale.CANADA, "%d", (now - fingerDownTime));
        float fingerDownUpDelta = (float)Math.sqrt((xSelect - xFingerDown) * (xSelect -
                xFingerDown) + (ySelect - yFingerDown) * (ySelect - yFingerDown));

		/*
         * CAUTION: Ensure this is consistent with the sd1 header line defined at the top of
		 * FittsDragAndDropActivity.
		 */
        sb1.append(String.format(Locale.CANADA, "%s,%s,%s,%s,%s,%s,%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s," +
                        "%s,%s,%d,%d,%d%s", participantCode, sessionCode, blockCode, groupCode, conditionCode,
                dimensionMode, selectionCount, aw[awIdx].a, aw[awIdx].w, expPanel.fromTarget.xCenter, expPanel
                        .fromTarget.yCenter, expPanel.toTarget.xCenter, expPanel.toTarget.yCenter, xFingerDown,
                yFingerDown, xSelect, ySelect, xDelta, fingerDownUpDelta, fingerDownUpTime, b, pickupMissCountTrial,
                trialError, trialTime, System.getProperty("line.separator")));

        // write trace data at end of trial (t, x, y)
        tracePoint.add(new TracePoint(now - trialStartTime, (int)xSelect, (int)ySelect));
        sd3Leadin = String.format(Locale.CANADA, "%s,%s,%s,%s,%d,%d,%d,%d,%d,%d,%d,%d,", APP,
                participantCode, conditionCode, blockCode, (awIdx + 1), (int)aw[awIdx].a, (int)aw[awIdx].w,
                (selectionCount + 1), (int)expPanel.fromTarget.xCenter, (int)expPanel.fromTarget.yCenter, (int)
                        expPanel.toTarget.xCenter, (int)expPanel.toTarget.yCenter);
        // time data
        sb3 = new StringBuilder(sd3Leadin);
        sb3.append("t=,");
        for (int i = 0; i < tracePoint.size(); ++i)
            sb3.append(tracePoint.get(i).t).append(',');
        sb3.append(System.getProperty("line.separator"));
        try
        {
            sd3.write(sb3.toString(), 0, sb3.length());
            sd3.flush();
        } catch (IOException e)
        {
            Toast.makeText(this, String.format("ERROR WRITING TO DATA FILE: e = %s", e), Toast.LENGTH_LONG).show();
        }

        // x data
        sb3 = new StringBuilder(sd3Leadin);
        sb3.append("x=,");
        for (int i = 0; i < tracePoint.size(); ++i)
            sb3.append(tracePoint.get(i).x).append(',');
        sb3.append(System.getProperty("line.separator"));
        try
        {
            sd3.write(sb3.toString(), 0, sb3.length());
            sd3.flush();
        } catch (IOException e)
        {
            Toast.makeText(this, String.format("ERROR WRITING TO DATA FILE: e = %s", e), Toast.LENGTH_LONG).show();
        }

        // y data
        sb3 = new StringBuilder(sd3Leadin);
        sb3.append("y=,");
        for (int i = 0; i < tracePoint.size(); ++i)
            sb3.append(tracePoint.get(i).y).append(',');
        sb3.append(System.getProperty("line.separator"));
        try
        {
            sd3.write(sb3.toString(), 0, sb3.length());
            sd3.flush();
        } catch (IOException e)
        {
            Toast.makeText(this, String.format("ERROR WRITING TO DATA FILE: e = %s", e), Toast.LENGTH_LONG).show();
        }

		/*
         * These four arrays are added to at the end of each trial. At the end of a sequence, they
		 * are passed to the Throughput constructor. The Throughput object will compute throughput
		 * and other values based on the data in these arrays.
		 */
        from[selectionCount] = new PointF(expPanel.fromTarget.xCenter, expPanel.fromTarget.yCenter);
        to[selectionCount] = new PointF(expPanel.toTarget.xCenter, expPanel.toTarget.yCenter);
        select[selectionCount] = new PointF(xSelect, ySelect);
        mtArray[selectionCount] = trialTime;

        // prepare for next target selection
        ++selectionCount;
        dragInProgress = false;
        pickupMissCountTrial = 0;

        // new... beginning of a trial (but not the first trial in the sequence)
        trialStartTime = now;
        tracePoint = new ArrayList<>();
        tracePoint.add(new TracePoint(0, (int)xSelect, (int)ySelect));

        advanceTarget();

        if (selectionCount == numberOfTrials) // finished sequence
            doEndSequence();
    }

    void doEndSequence()
    {
        if (outlier)
        {
            ++outlierSequenceCount;
            results.append("Oops! Outlier sequence!::Possible causes...:- missed tap:- double " +
                    "tap::Tap to try again");
            expPanel.resultsString = results.toString().split(":");
            outlier = false;
        } else
        {
            /*
             * Give the relevant data to the Throughput object (where the serious calculations are
			 * performed).
			 */
            int taskType = dimensionMode.equals("1D") ? Throughput.ONE_DIMENSIONAL : Throughput.TWO_DIMENSIONAL;
            int responseType = Throughput.SERIAL;
            Throughput t = new Throughput("nocode", aw[awIdx].a, aw[awIdx].w, taskType, responseType, from, to, select,
                    mtArray);
            /*
             * CAUTION: Ensure this is consistent with the sd2 header line defined at the top of
			 * FittsDragAndDropActivity.
			 */
            sb2.append(sd2Header);
            sb2.append(String.format(Locale.CANADA, ",%d,%s,%s,%s,%s,%s,%s,%d,%d,%s,%s,%s%s", t.getNumberOfTrials(),
                    t.getA(), t.getW(), t.getID(), t.getAe(), t.getWe(), t.getIDe(), pickupMissCountSequence,
                    outlierSequenceCount, t.getMT(), t.getErrorRate(), t.getThroughput(), System.getProperty("line" +
                            ".separator")));

            // write data to files at end of each sequence
            try
            {
                sd1.write(sb1.toString(), 0, sb1.length());
                sd1.flush();
                sd2.write(sb2.toString(), 0, sb2.length());
                sd2.flush();
            } catch (IOException e)
            {
                Toast.makeText(this, String.format("ERROR WRITING TO DATA FILE: e = %s", e), Toast.LENGTH_LONG).show();
            }
            sb1.delete(0, sb1.length());
            sb2.delete(0, sb2.length());

            // prepare results for output on display
            StringBuilder s = new StringBuilder();
            s.append(String.format(Locale.CANADA, "Block %d:", Integer.parseInt(blockCode.substring(1))));
            s.append(String.format(Locale.CANADA, "Sequence %d of %d:", (awIdx + 1), aw.length));
            s.append(String.format(Locale.CANADA, "Number of trials = %d:", t.getNumberOfTrials()));
            s.append(String.format(Locale.CANADA, "A = %d px (nominal):", Math.round(t.getA())));
            s.append(String.format(Locale.CANADA, "W = %d px:", Math.round(t.getW())));
            s.append(String.format(Locale.CANADA, "ID = %.2f bits:", t.getID()));
            s.append("-----:");
            s.append(String.format(Locale.CANADA, "Ae = %.1f px:", t.getAe()));
            s.append(String.format(Locale.CANADA, "We = %.1f px:", t.getWe()));
            s.append(String.format(Locale.CANADA, "IDe = %.2f bits:", t.getIDe()));
            s.append(String.format(Locale.CANADA, "Pickup misses = %d:", pickupMissCountSequence));
            s.append(String.format(Locale.CANADA, "MT = %d ms (per trial):", Math.round(t.getMT())));
            s.append(String.format(Locale.CANADA, "Drop errors = %d:", t.getMisses()));
            s.append(String.format(Locale.CANADA, "Throughput = %.2f bps:", t.getThroughput()));
            results.append(s);
            expPanel.resultsString = results.toString().split(":");

            if (fittsFarmStyle && speechFeedback)
                thankYou.start();

            ++awIdx; // next A-W condition
            if (awIdx < aw.length)
            {
                configureTargets(awIdx);

            } else
            {
                expPanel.done = true;
            }
            outlierSequenceCount = 0;
            pickupMissCountSequence = 0;
            pickupMissCountTrial = 0;
        }
        expPanel.waitStartCircleSelect = true;
        expPanel.showBanner = false; // only show before 1st sequence
        sequenceStarted = false;
        dragInProgress = false;
    }

    // advance to the next target (a bit complicated for the 2D task; see comment below)
    private void advanceTarget()
    {
        /*
         * Find the current "target" then advance it to the circle on the opposite side of the layout circle. This is
         * a bit tricky since every second advance requires the target to be beside the target directly opposite the
         * last target. This is needed to get the sequence of selections to advance around the layout circle. Of
         * course, this only applies to the 2D task.
         */

        // find index of current target
        int i;
        for (i = 0; i < expPanel.targetSet.length; ++i)
            if (expPanel.targetSet[i].status == Target.TARGET)
                break; // i is index of current target

        // last target becomes "normal" again
        expPanel.targetSet[i].status = Target.NORMAL;
        expPanel.xDragObject = expPanel.targetSet[i].xCenter;
        expPanel.yDragObject = expPanel.targetSet[i].yCenter;

        // find next target
        int next;
        if (dimensionMode.equals("1D"))
        {
            next = (i + 1) % 2;
        } else
        {
            int halfWay = (expPanel.targetSet.length + 1) / 2;
            next = (i + halfWay) % expPanel.targetSet.length; // NOTE: odd number of targets
        }

        expPanel.targetSet[next].status = Target.TARGET;
        expPanel.fromTarget = expPanel.targetSet[i];
        expPanel.toTarget = expPanel.targetSet[next];

        expPanel.nextRandomGraphic(); // only relevant for FittsFarm mode
    }

    /*
 * Get the device's default/natural orientation. The default orientation is a function of the
 * current orientation combined with the current rotation. See...
 *
 * http://stackoverflow.com/questions/4553650/how-to-check-device-natural-default-orientation
 * -on-
 * android-i-e-get-landscape
 */
    public int getDefaultOrientation()
    {
        int orientation = this.getResources().getConfiguration().orientation;
        int rotation = getCurrentRotation();

        if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && orientation
                == Configuration.ORIENTATION_LANDSCAPE)
                || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) &&
                orientation == ORIENTATION_PORTRAIT))
            return Configuration.ORIENTATION_LANDSCAPE;
        else
            return Configuration.ORIENTATION_PORTRAIT;
    }

    // return the current rotation of the display
    public int getCurrentRotation()
    {
        return this.getWindowManager().getDefaultDisplay().getRotation();
    }

    // simple class to hold the amplitude and width for a Fitts' law task
    private class AmplitudeWidth
    {
        float a, w;

        AmplitudeWidth(float aArg, float wArg)
        {
            a = aArg;
            w = wArg;
        }
    }
}