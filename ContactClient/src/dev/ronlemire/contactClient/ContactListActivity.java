package dev.ronlemire.contactClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ContactListActivity extends Activity {
	public static String LIST_CONTACT_ID; // View contact Intent key
	private TableLayout queryTableLayout; // shows the search buttons
	private ProgressBar pbLoading;
	private TextView tvLoading;

	// *****************************************************************************
	// Activity LifeCycle Event Handlers
	// *****************************************************************************
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list);

		Button runServiceButton = (Button) findViewById(R.id.runServiceButton);
		runServiceButton.setOnClickListener(refreshButtonListener);

		// get a reference to the queryTableLayout
		queryTableLayout = (TableLayout) findViewById(R.id.contactsTableLayout);

		// btnRun = (Button) this.findViewById(R.id.runServiceButton);
		// btnRun.setVisibility(View.INVISIBLE);
		pbLoading = (ProgressBar) this.findViewById(R.id.pbLoading);
		pbLoading.setVisibility(View.INVISIBLE);
		tvLoading = (TextView) this.findViewById(R.id.tvLoading);
		tvLoading.setVisibility(View.INVISIBLE);
		GetContactListAfterCheckingForURLs();
	}

	// *****************************************************************************
	// Before getting ContactList make sure URLs are setup
	// *****************************************************************************
	private void GetContactListAfterCheckingForURLs() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getString("multipleContactUrl", null) == null
				|| prefs.getString("singleContactUrl", null) == null) {
			this.startActivity(new Intent(this, PrefsActivity.class)
					.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG)
					.show();
		} else {
			GetContactsList();
		}
	}

	// *****************************************************************************
	// Options Menu (ContactAddActivity, PrefsActivity)
	// *****************************************************************************
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_contact, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) // switch based on selected MenuItem's ID
		{
		case R.id.addContactItem:
			Intent addEditContact = new Intent(this,
					ContactAddEditActivity.class);
			addEditContact.putExtra("id", "0");
			addEditContact.putExtra("firstName", "");
			addEditContact.putExtra("lastName", "");
			startActivity(addEditContact);
			return true;
		case R.id.prefsItem:
			startActivity(new Intent(this, PrefsActivity.class)
					.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// *****************************************************************************
	// RefreshList Button listener
	// *****************************************************************************
	public OnClickListener refreshButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			GetContactListAfterCheckingForURLs();
		}
	};

	public void GetContactsList() {
		TableLayout table = (TableLayout) findViewById(R.id.contactsTableLayout);
		table.removeAllViews();

		pbLoading.setVisibility(View.VISIBLE);
		tvLoading.setVisibility(View.VISIBLE);

		new GetContactListAsyncTask("", this, new ContactListLoadedListener())
				.execute("0");
	}

	// *****************************************************************************
	// GetContactList on a separate thread
	// *****************************************************************************
	public interface ContactListLoadedListenerInterface {
		public void onContactListLoaded(Contact[] contactsArray);
	}

	public class GetContactListAsyncTask extends
			AsyncTask<Object, Object, String> {
		String idParameterString;
		private Context context;
		private Contact[] contactsArray;

		// listener for retrieved ContactList
		private ContactListLoadedListener contactListLoadedListener;

		public GetContactListAsyncTask(
				String idParameterString,
				Context context,
				dev.ronlemire.contactClient.ContactListActivity.ContactListLoadedListener contactListLoadedListener) {
			this.idParameterString = idParameterString;
			this.context = context;
			this.contactListLoadedListener = (ContactListLoadedListener) contactListLoadedListener;
		}

		// Call ContactService to get ContactList
		@Override
		protected String doInBackground(Object... params) {
			ContactHttpClient contactHttpClient = new ContactHttpClient(
					this.context);
			contactsArray = contactHttpClient
					.GetContactArray((String) params[0]);

			return null; // return null if the city name couldn't be found
		}

		// executed back on the UI thread after the city name loads
		protected void onPostExecute(String domainString) {
			contactListLoadedListener.onContactListLoaded(contactsArray);
		} // end method onPostExecute

	}

	// listens for contacts loaded in background task
	public class ContactListLoadedListener implements
			ContactListLoadedListenerInterface {

		// create a new CityNameLocationLoadedListener
		public ContactListLoadedListener() {
		} // end CityNameLocationLoadedListener

		@Override
		public void onContactListLoaded(Contact[] contactsArray) {
			for (int i = 0; i < contactsArray.length; i++) {
				makeNewContactRow(((Contact) contactsArray[i]).getId(),
						contactsArray[i].getFirstName(),
						contactsArray[i].getLastName(), i);
			}

			pbLoading.setVisibility(View.INVISIBLE);
			tvLoading.setVisibility(View.INVISIBLE);
		}
	}

	// ***************************************************************************************
	// Inflate new ContactList row.
	// =======================================================================================
	// Note: Id column value is underlined and has a listener to start ContactViewActivity.
	// ***************************************************************************************
	private void makeNewContactRow(String ID, String FirstName,
			String LastName, int index) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View newContactRowView = inflater.inflate(R.layout.contact_list_row,
				null);

		TextView newIDTextView = (TextView) newContactRowView
				.findViewById(R.id.tvContactID);
		SpannableString content = new SpannableString(ID);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		newIDTextView.setText(content);
		newIDTextView.setOnClickListener(idTextViewListener);

		TextView newFirstNameTextView = (TextView) newContactRowView
				.findViewById(R.id.tvContactFirstName);
		newFirstNameTextView.setText(FirstName);

		TextView newLastNameTextView = (TextView) newContactRowView
				.findViewById(R.id.tvContactLastName);
		newLastNameTextView.setText(LastName);

		// Add new row to ContactList
		queryTableLayout.addView(newContactRowView, index);
	}

	// *****************************************************************************
	// ContactId Column click listener
	// *****************************************************************************
	public OnClickListener idTextViewListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ContactListActivity.LIST_CONTACT_ID = ((TextView) v).getText()
					.toString();

			Intent viewContact = new Intent(ContactListActivity.this,
					ContactViewActivity.class);
			startActivity(viewContact); // start the ViewContact Activity
		}
	};
}
