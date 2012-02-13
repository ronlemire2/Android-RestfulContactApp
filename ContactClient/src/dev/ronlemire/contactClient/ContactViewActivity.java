package dev.ronlemire.contactClient;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
//import android.database.Cursor;
//import android.os.AsyncTask;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.widget.EditText;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class ContactViewActivity extends Activity {
	private TextView idTextView;
	private TextView firstNameTextView;
	private TextView lastNameTextView;
	private TextView emailTextView;

	// *****************************************************************************
	// Activity LifeCycle Event Handlers
	// *****************************************************************************
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_view);

		idTextView = (TextView) findViewById(R.id.idTextView);
		firstNameTextView = (TextView) findViewById(R.id.firstNameTextView);
		lastNameTextView = (TextView) findViewById(R.id.lastNameTextView);
		emailTextView = (TextView) findViewById(R.id.emailTextView);
	}

	@Override
	protected void onResume() {
		super.onResume();

		new GetContactAsyncTask("", this, new ContactLoadedListener())
				.execute(ContactListActivity.LIST_CONTACT_ID);
	}

	// **********************************************************************************
	// Get Contact on a separate thread.
	// ==================================================================================
	// Note: Contact will be returned by ContactService in a one element ContactArray.
	// **********************************************************************************
	public interface ContactLoadedListenerInterface {
		public void onContactLoaded(Contact[] contactsArray);
	}

	class GetContactAsyncTask extends AsyncTask<Object, Object, String> {
		String idParameterString;
		private Context context; // launching Activity's Context
		private Contact[] contactsArray;

		private ContactLoadedListener contactLoadedListener;

		public GetContactAsyncTask(String idParameterString, Context context,
				ContactLoadedListener contactLoadedListener) {
			this.idParameterString = idParameterString;
			this.context = context;
			this.contactLoadedListener = contactLoadedListener;
		}

		// Call ContactService to get Contact
		@Override
		protected String doInBackground(Object... params) {
			ContactHttpClient contactHttpClient = new ContactHttpClient(
					this.context);
			contactsArray = contactHttpClient
					.GetContactArray((String) params[0]);
			return null;
		}

		// executed back on the UI thread after the contact loads
		protected void onPostExecute(String domainString) {
			contactLoadedListener.onContactLoaded(contactsArray);
		}

	}

	public class ContactLoadedListener implements ContactLoadedListenerInterface {
		public ContactLoadedListener() {
		} 

		@Override
		public void onContactLoaded(Contact[] contactsArray) {
			if (contactsArray != null) {
				idTextView.setText(contactsArray[0].getId());
				firstNameTextView.setText(contactsArray[0].getFirstName());
				lastNameTextView.setText(contactsArray[0].getLastName());
				emailTextView.setText(contactsArray[0].getEmail());
			}
		}
	}

	// *****************************************************************************
	// Options Menu (ContactEditActivity, ContactDeleteActivity, PrefsActivity)
	// *****************************************************************************
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_contact, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) // switch based on selected MenuItem's ID
		{
		case R.id.editItem:
			Intent addEditContact = new Intent(this,
					ContactAddEditActivity.class);
			addEditContact.putExtra("id", idTextView.getText());
			addEditContact.putExtra("firstName", firstNameTextView.getText());
			addEditContact.putExtra("lastName", lastNameTextView.getText());
			addEditContact.putExtra("email", emailTextView.getText());
			startActivity(addEditContact);
			return true;
		case R.id.deleteItem:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure?")
					.setPositiveButton("Yes", deleteContactClickListener)
					.setNegativeButton("No", deleteContactClickListener).show();

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
	// Delete Contact - Are you sure?  listener
	// *****************************************************************************
	DialogInterface.OnClickListener deleteContactClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				if (idTextView.getText().length() != 0) {
					String id = idTextView.getText().toString();
					String firstName = firstNameTextView.getText().toString();
					String lastName = lastNameTextView.getText().toString();
					String email = emailTextView.getText().toString();
					Contact deleteContact = new Contact(id, firstName,
							lastName, email);
					new DeleteContactAsyncTask(deleteContact,
							ContactViewActivity.this,
							new ContactDeletedListener())
							.execute(deleteContact);
				}
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				// No button clicked
				break;
			}
		}
	};

	// *****************************************************************************
	// Delete Contact on a separate thread
	// *****************************************************************************
	public interface ContactDeletedListenerInterface {
		public void onContactDeleted(Integer statusCode);
	}

	class DeleteContactAsyncTask extends AsyncTask<Object, Object, Integer> {
		private ContactDeletedListener contactDeletedListener;
		private Context taskContext;
		HttpResponse response;

		// public constructor
		public DeleteContactAsyncTask(Contact deleteContact, Context context,
				ContactDeletedListener listener) {
			this.contactDeletedListener = listener;
			this.taskContext = context;
		}

		// call ContactService to delete Contact
		@Override
		protected Integer doInBackground(Object... params) {
			Contact deleteContact = (Contact) params[0];
			ContactHttpClient contactHttpClient = new ContactHttpClient(
					this.taskContext);
			return contactHttpClient.DeleteContact(deleteContact);
		}

		// executed back on the UI thread after the contact deleted
		protected void onPostExecute(Integer statusCode) {
			contactDeletedListener.onContactDeleted(statusCode);
		}
	}

	private class ContactDeletedListener implements
			ContactDeletedListenerInterface {

		public ContactDeletedListener() {
		}

		@Override
		public void onContactDeleted(Integer statusCode) {
			if (statusCode == 200) {
				Toast message = Toast.makeText(ContactViewActivity.this,
						R.string.contactDeleted, Toast.LENGTH_SHORT);
				message.setGravity(Gravity.CENTER, message.getXOffset(),
						message.getYOffset());
				message.show();
			} else {
				Toast message = Toast
						.makeText(ContactViewActivity.this,
								R.string.contactDeletedErrorMessage,
								Toast.LENGTH_SHORT);
				message.setGravity(Gravity.CENTER, message.getXOffset(),
						message.getYOffset());
				message.show();
			}

			// return to the Contact List
			Intent getContacts = new Intent(ContactViewActivity.this,
					ContactListActivity.class);
			getContacts.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(getContacts);

		}
	}
}
