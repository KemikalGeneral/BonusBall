package com.example.kemik.bonusball;

import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kemik.bonusball.Database.DBHelper;
import com.example.kemik.bonusball.Entities.Draw;
import com.example.kemik.bonusball.Entities.Entrant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class DrawDetail extends AppCompatActivity
        implements ConfirmationDialog.ConfirmationDialogListener {

    private DBHelper db;
    private EntrantArrayAdapter entrantArrayAdapter;
    private TextView tv_drawName;
    private TextView tv_startDate;
    private TextView tv_drawValue;
    private TextView tv_ticketValue;
    private TextView tv_profit;
    private ListView lv_numberSlotListView;
    private FloatingActionButton fab_options;
    private FloatingActionButton fab_randoms;
    private FloatingActionButton fab_numbers;
    private FloatingActionButton fab_names;
    private FloatingActionButton fab_edit;
    private FloatingActionButton fab_delete;
    private EditText et_searchBar;
    private boolean isOpen = false;
    private long drawId;

    private ConstraintLayout cl_copyableWindowContainer;
    private TextView tv_copyableTextWindow;
    private ImageView iv_copyableTextWindowCloseIcon;

    // Randomiser
    private ConstraintLayout cl_randomiserLayout;
    private EditText et_randomiserName;
    private EditText et_amountOfNumbers;
    private Button btn_generateRandoms;
    private EditText et_returnedRandoms;
    private Button btn_randomiserCancel;
    private Button btn_randomiserAccept;
    private boolean isReady = false;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_detail);

        // Find all views
        findViews();

        // Instantiate a new DBHelper class
        db = new DBHelper(this);

        // Get ID from intent extras
        Intent intent = getIntent();
        drawId = intent.getLongExtra("DrawId", 0);

        // Create the Draw from the ID
        Draw draw = db.getDrawById(drawId);

        // Populate TextViews with Draw details
        populateDrawDetailsTextViews(draw);

        // Set up and display the ListView
        setupAndDisplayListView(draw);

//        fab_randoms.setVisibility(View.INVISIBLE);
//        fab_numbers.setVisibility(View.INVISIBLE);
//        fab_names.setVisibility(View.INVISIBLE);
//        fab_edit.setVisibility(View.INVISIBLE);
//        fab_delete.setVisibility(View.INVISIBLE);
//        et_searchBar.setVisibility(View.INVISIBLE);

        // Toggle visibility on FAB click for Edit and Delete
        fab_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpen) {
                    openFabs();
                } else {
                    closeFabs();
                }
            }
        });

        iv_copyableTextWindowCloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeCopyableWindow();
            }
        });
    }

    /**
     * Find all views by their ID's
     */
    private void findViews() {
        tv_drawName = findViewById(R.id.drawName);
        tv_startDate = findViewById(R.id.startDate);
        tv_drawValue = findViewById(R.id.drawValue);
        tv_ticketValue = findViewById(R.id.ticketValue);
        tv_profit = findViewById(R.id.profit);
        lv_numberSlotListView = findViewById(R.id.numberSlotListView);
        fab_options = findViewById(R.id.drawDetailFab);
        fab_randoms = findViewById(R.id.drawDetailRandomsFab);
        fab_numbers = findViewById(R.id.drawDetailNumbersFab);
        fab_names = findViewById(R.id.drawDetailNamesFab);
        fab_edit = findViewById(R.id.drawDetailEditFab);
        fab_delete = findViewById(R.id.drawDetailDeleteFab);

        // Search bar
        et_searchBar = findViewById(R.id.drawDetailSearch);

        // Copyable text window
        cl_copyableWindowContainer = findViewById(R.id.copyableTextWindowContainer);
        tv_copyableTextWindow = findViewById(R.id.copyableTextWindow);
        iv_copyableTextWindowCloseIcon = findViewById(R.id.copyableTextWindowCloseButton);

        // Randomiser
        cl_randomiserLayout = findViewById(R.id.randomiserLayout);
        et_randomiserName = findViewById(R.id.randomiserName);
        et_amountOfNumbers = findViewById(R.id.randomiserAmountOfNumbers);
        btn_generateRandoms = findViewById(R.id.randomiserGenerateButton);
        et_returnedRandoms = findViewById(R.id.randomiserRandomNumbers);
        btn_randomiserCancel = findViewById(R.id.randomiserCancelButton);
        btn_randomiserAccept = findViewById(R.id.randomiserAcceptButton);
    }

    /**
     * Populate the TextViews for showing the details of the current Draw from the DB
     *
     * @param draw
     */
    private void populateDrawDetailsTextViews(Draw draw) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
        String dateString = simpleDateFormat.format(date);
        double profit = (draw.getTicketValue() * 59) - draw.getDrawValue();

        tv_drawName.setText(draw.getDrawName());
        tv_startDate.setText(dateString);
        tv_drawValue.setText("£");
        tv_drawValue.append(String.format("%.2f", draw.getDrawValue()));
        tv_ticketValue.setText("£");
        tv_ticketValue.append(String.format("%.2f", draw.getTicketValue()));
        tv_profit.setText("£");
        tv_profit.append(String.format("%.2f", profit));
    }

    /**
     * SetUp and display the ListView according to the state of the searchBar (populated or not)
     * ...Create a new ArrayAdapter
     * ...AddAll Draws from DB
     * ...Set adapter
     */
    private void setupAndDisplayListView(final Draw draw) {
        // Create a new adapter and add all of the Entrants for this Draw
        entrantArrayAdapter = new EntrantArrayAdapter(this, draw.getDrawId());
        entrantArrayAdapter.addAll(db.getEntrantsByDrawId(draw.getDrawId()));

        // On entry of text in to the search bar, lookup names LIKE 'name'
        // and populate the adapter with the results.
        // If the name filed is empty, show all details of this Draw
        et_searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = et_searchBar.getText().toString().trim();
                entrantArrayAdapter.clear();

                if (name.equals("")) {
                    entrantArrayAdapter.addAll(db.getEntrantsByDrawId(draw.getDrawId()));
                } else {
                    entrantArrayAdapter.addAll(db.getFilteredEntrantsByDrawId(name, drawId));
                }

                lv_numberSlotListView.setAdapter(entrantArrayAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        lv_numberSlotListView.setAdapter(entrantArrayAdapter);
    }

    /**
     * Make the required views visible and set some onClick listeners, when the FAB is opened
     */
    private void openFabs() {
        isOpen = true;
        fab_options.setImageResource(R.drawable.close);
        fab_options.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey)));

        fab_randoms.setVisibility(View.VISIBLE);
        fab_randoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateRandomNumber();
            }
        });

        fab_numbers.setVisibility(View.VISIBLE);
        fab_numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemainingNumbers();
            }
        });

        fab_names.setVisibility(View.VISIBLE);
        fab_names.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNamesAndNumbers();
            }
        });

        fab_edit.setVisibility(View.VISIBLE);
        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDraw(drawId);
            }
        });

        fab_delete.setVisibility(View.VISIBLE);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDraw(drawId);
            }
        });

        et_searchBar.setVisibility(View.VISIBLE);

        fabOpenAnimation();
    }

    /**
     * When required, an EditText fields shows containing copyable data
     */
    private void openCopyableWindow() {
        cl_copyableWindowContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Make the required views gone, when the FAB is closed
     */
    private void closeFabs() {
        fabCloseAnimation();

        isOpen = false;
        fab_options.setImageResource(R.drawable.more_menu);
        fab_options.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        fab_randoms.setVisibility(View.GONE);
        fab_numbers.setVisibility(View.GONE);
        fab_names.setVisibility(View.GONE);
        fab_edit.setVisibility(View.GONE);
        fab_delete.setVisibility(View.GONE);
        et_searchBar.setVisibility(View.GONE);
    }

    /**
     * When not longer required, close the EditText (and subsequent close icon)
     */
    private void closeCopyableWindow() {
        cl_copyableWindowContainer.setVisibility(View.GONE);
    }

    /**
     * Populate and make visible a hidden EditText field with the remaining numbers.
     * Copies remaining numbers to clipboard for ease of pasting elsewhere.
     */
    private void showRemainingNumbers() {
        ArrayList<Integer> remainingNumbers = db.getRemainingNumbers(drawId);
        int size = remainingNumbers.size();

        closeFabs();
        openCopyableWindow();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Available...\n\n* ");
        tv_copyableTextWindow.setText("Available...\n\n* ");
        for (int i = 0; i < size; i++) {
            // Print to screen
            tv_copyableTextWindow.append(String.valueOf(remainingNumbers.get(i)));
            tv_copyableTextWindow.append(" * ");

            // Add to string builder for clipboard
            stringBuilder.append(String.valueOf(remainingNumbers.get(i)));
            stringBuilder.append(" * ");
        }

        // Copy to clipboard for ease of pasting
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("remainingNumbers", stringBuilder.toString());
        clipboardManager.setPrimaryClip(data);
        Toast.makeText(this, "Remaining numbers copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

    /**
     * On FAB randomiser click, close FABs and clear the randomiser fields.
     * Set up Cancel Button.
     * Generate random numbers to an array.
     * Setup Accept Button to save to db.
     */
    public void generateRandomNumber() {
        fab_options.setVisibility(View.GONE);
        closeFabs();
        cl_randomiserLayout.setVisibility(View.VISIBLE);
        et_randomiserName.setText(null);
        et_amountOfNumbers.setText(null);
        et_returnedRandoms.setText(null);
        isReady = false;

        // Close window on Cancel Button click
        randomiserCancelButton();

        // Generate required amount of random numbers
        ArrayList<Entrant> entrants = randomiserGenerateButton();

        // Save name and numbers to db
        randomiserAcceptButton(entrants);
    }

    /**
     * Close the randomiser window on Cancel Button click
     */
    private void randomiserCancelButton() {
        btn_randomiserCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cl_randomiserLayout.setVisibility(View.GONE);
                fab_options.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Gets the name and amount of requested numbers.
     * Gets the [user requested] amount of random numbers from the remainingNumbers.
     * Displays them to the EditText returned randoms field.
     * Copies random numbers to the clipboard.
     */
    private ArrayList<Entrant> randomiserGenerateButton() {
        final ArrayList<Entrant> entrants = new ArrayList<>();

        btn_generateRandoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String randomiserName = null;
                int randomiserAmountOfNumbers = 0;

                // Clear the Entrants array to avoid duplicate entries on multiple generations
                entrants.clear();

                // Validate and get name
                if (et_randomiserName.getText().toString().trim().equals("")) {
                    Toast.makeText(DrawDetail.this, "Who are these for...?", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    randomiserName = et_randomiserName.getText().toString().trim();
                }

                // Validate and get amount of numbers required
                if (et_amountOfNumbers.getText().toString().trim().equals("")) {
                    Toast.makeText(DrawDetail.this, "How many numbers...?", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    randomiserAmountOfNumbers = Integer.parseInt(et_amountOfNumbers.getText().toString().trim());
                }

                // Get a list of the remaining numbers for this draw
                ArrayList<Integer> remainingNumbers = db.getRemainingNumbers(drawId);

                // Check that there are enough numbers
                if (randomiserAmountOfNumbers > remainingNumbers.size()) {
                    Toast.makeText(DrawDetail.this, "There are only " + remainingNumbers.size() + " numbers left!", Toast.LENGTH_SHORT).show();
                } else {
                    // Randomise the remaining numbers
                    Collections.shuffle(remainingNumbers);

                    // Create a new array and add the amount if random numbers requested
                    ArrayList<Integer> randomNumbersArray = new ArrayList<>();
                    for (int i = 0; i < randomiserAmountOfNumbers; i++) {
                        randomNumbersArray.add(remainingNumbers.get(i));
                    }

                    // Sort the array (low-hi)
                    Collections.sort(randomNumbersArray);
                    // Iterate through the array, displaying the numbers to the EditText field ready for copying.
                    // Create and Entrant from the name and number and add it to the list of entrants,
                    // to be passed for saving.
                    // Create a stringBuilder for copying to the clipboard.
                    et_returnedRandoms.setText("* ");
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("* ");
                    for (int i = 0; i < randomNumbersArray.size(); i++) {
                        // Display to screen
                        et_returnedRandoms.append(String.valueOf(randomNumbersArray.get(i)));
                        et_returnedRandoms.append(" * ");

                        // Add to stringBuilder
                        stringBuilder.append(String.valueOf(randomNumbersArray.get(i)));
                        stringBuilder.append(" * ");

                        Entrant entrant = new Entrant();
                        entrant.setEntrantName(randomiserName);
                        entrant.setLineNumber(randomNumbersArray.get(i));
                        entrants.add(entrant);
                    }

                    // Copy to clipboard
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData data = ClipData.newPlainText("generatedNumbers", stringBuilder.toString());
                    clipboardManager.setPrimaryClip(data);
                    Toast.makeText(DrawDetail.this, "Random numbers copied to clipboard", Toast.LENGTH_LONG).show();

                    // Set isReady to true, so that the Accept Button saves name and numbers to db
                    isReady = true;
                }
            }
        });
        return entrants;
    }

    /**
     * Take a list of Entrants (names are all the same name but with the generated random numbers),
     * loop through it and save them to the db using the drawId.
     *
     * @param entrants
     */
    private void randomiserAcceptButton(final ArrayList<Entrant> entrants) {
        btn_randomiserAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReady) {
                    String name;
                    int number;

                    // Save names and numbers against drawId
                    for (int i = 0; i < entrants.size(); i++) {
                        name = entrants.get(i).getEntrantName();
                        number = entrants.get(i).getLineNumber();
                        db.updateNameToChosenNumber(name, number, drawId);
                    }

                    // Close randomiser and recreate activity
                    cl_randomiserLayout.setVisibility(View.GONE);
                    recreate();
                } else {
                    Toast.makeText(DrawDetail.this, "You haven't generated any numbers!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Populate and make visible a hidden EditText field with the list of names and numbers.
     * Copies remaining numbers to clipboard for ease of pasting elsewhere.
     */
    private void showNamesAndNumbers() {
        ArrayList<Entrant> finalNamesAndNumbers = db.getEntrantsByDrawId(drawId);
        int size = finalNamesAndNumbers.size();

        closeFabs();
        openCopyableWindow();

        StringBuilder stringBuilder = new StringBuilder();
        tv_copyableTextWindow.setText("");
        for (int i = 0; i < size; i++) {
            tv_copyableTextWindow.append(String.valueOf(finalNamesAndNumbers.get(i).getLineNumber()));
            tv_copyableTextWindow.append(" - ");

            // Add to string builder for clipboard
            stringBuilder.append(String.valueOf(finalNamesAndNumbers.get(i).getLineNumber()));
            stringBuilder.append(" - ");

            // Append name if one exists
            if (finalNamesAndNumbers.get(i).getEntrantName() != null) {
                tv_copyableTextWindow.append(finalNamesAndNumbers.get(i).getEntrantName());
                stringBuilder.append(finalNamesAndNumbers.get(i).getEntrantName());
            }

            tv_copyableTextWindow.append("\n");
            stringBuilder.append("\n");
        }

        // Copy to clipboard for ease of pasting
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("namesAndNumbers", stringBuilder.toString());
        clipboardManager.setPrimaryClip(data);
        Toast.makeText(this, "Name and Numbers copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

    /**
     * On edit click, update the draw details using the drawId
     *
     * @param drawId
     */
    private void editDraw(long drawId) {
        Intent editIntent = new Intent(DrawDetail.this, EditDraw.class);
        editIntent.putExtra("DrawId", drawId);
        startActivity(editIntent);
        finish();
    }

    /**
     * Show ConfirmationDialog box to delete a Draw
     *
     * @param drawId
     */
    private void deleteDraw(long drawId) {
        DialogFragment dialogFragment = new ConfirmationDialog();
        dialogFragment.show(getFragmentManager(), "ConfirmationDialog");
    }

    /**
     * On positive click, delete Draw by drawId, which also removes its associated Entrants
     *
     * @param dialogFragment
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        db.deleteDraw(drawId);

        startActivity(new Intent(DrawDetail.this, MainActivity.class));
        finish();
    }

    /**
     * On negative click, do nothing
     *
     * @param dialogFragment
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialogFragment) {
        // Do nothing
    }

    /**
     * Animations to be used when opening the FABs
     */
    private void fabOpenAnimation() {
        Animation animationSearch = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_stretch_open);
        Animation animationDel = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_slide_in);
        Animation animationEdit = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_slide_in);
        Animation animationNames = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_slide_in);
        Animation animationNumbers = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_slide_in);
        Animation animationRandoms = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_slide_in);

        et_searchBar.startAnimation(animationSearch);
        animationSearch.setStartOffset(delay(1));
        fab_delete.startAnimation(animationDel);
        animationDel.setStartOffset(delay(2));
        fab_edit.startAnimation(animationEdit);
        animationEdit.setStartOffset(delay(3));
        fab_names.startAnimation(animationNames);
        animationNames.setStartOffset(delay(4));
        fab_numbers.startAnimation(animationNumbers);
        animationNumbers.setStartOffset(delay(5));
        fab_randoms.startAnimation(animationRandoms);
        animationRandoms.setStartOffset(delay(6));
    }

    /**
     * Animations to be shown whe closing the FABs
     */
    private void fabCloseAnimation() {
        Animation animationRandoms = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_slide_out);
        Animation animationNumbers = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_slide_out);
        Animation animationNames = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_slide_out);
        Animation animationEdit = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_slide_out);
        Animation animationDel = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_slide_out);
        Animation animationSearch = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_stretch_close);

        fab_randoms.startAnimation(animationRandoms);
        animationRandoms.setStartOffset(delay(1));
        fab_numbers.startAnimation(animationNumbers);
        animationNumbers.setStartOffset(delay(2));
        fab_names.startAnimation(animationNames);
        animationNames.setStartOffset(delay(3));
        fab_edit.startAnimation(animationEdit);
        animationEdit.setStartOffset(delay(4));
        fab_delete.startAnimation(animationDel);
        animationDel.setStartOffset(delay(5));
        et_searchBar.startAnimation(animationSearch);
        animationSearch.setStartOffset(delay(6));

        // Clear the animations so that it releases the onClick listener to each FAB
        // ! This is required to fix a bug where the views are still clickable even when not
        // ! visible, and has an animation listener set to the search bar view as it is the
        // ! last view in the sequence of the FAB closing animation.
        animationSearch.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Do nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab_randoms.clearAnimation();
                fab_numbers.clearAnimation();
                fab_names.clearAnimation();
                fab_edit.clearAnimation();
                fab_delete.clearAnimation();
                et_searchBar.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing
            }
        });
    }

    /**
     * Used by fab...Animation methods for calculating the staggered delay by order
     *
     * @param order
     * @return
     */
    private long delay(int order) {

        return (long) (50 * order);

    }
}
