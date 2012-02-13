package dev.ronlemire.contactClient;

import org.apache.http.HttpResponse;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactAddEditActivity extends Activity {
	private EditText idEditText;
	private EditText firstNameEditText;
	private EditText lastNameEditText;
	private EditText emailEditText;
	private Button saveContactButton;

	// *****************************************************************************
	// Activity LifeCycle Event Handlers
	// *****************************************************************************
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // call super's onCreate
		setContentView(R.layout.contact_add_update); // inflate the UI

		idEditText = (EditText) findViewById(R.id.idEditText);
		firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
		lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
		emailEditText = (EditText) findViewById(R.id.emailEditText);
		saveContactButton = (Button) findViewById(R.id.saveContactButton);

		// if there are extras, use them to populate the EditTexts
		Bundle extras = getIntent().getExtras(); 
		if (extras != null) {
			idEditText.setText(extras.getString("id"));
			idEditText.setEnabled(false);
			firstNameEditText.setText(extras.getString("firstName"));
			lastNameEditText.setText(extras.getString("lastName"));
			emailEditText.setText(extras.getString("email"));
		} 
		
		saveContactButton.setText(idEditText.getText().toString().equals("0") ? getString(R.string.button_add_contact) : getString(R.string.button_update_contact));

		// set event listener for the Save Contact Button
		Button saveContactButton = (Button) findViewById(R.id.saveContactButton);
		saveContactButton.setOnClickListener(saveContactButtonClicked);
	} 

	// *****************************************************************************
	// Save Contact button click listener.
	// =============================================================================
	// Important: Save button has 2 purposes:
	//		1) If the Contact Id is zero(0) we are in Add Contact mode and the 
	//			button's label will be "Add Contact"
	//		2) If the Contact Id is not zero(0) we are in Update Contact mode of
	//			and existing Contact and the button's label will be "Update Contact"
	// *****************************************************************************
	OnClickListener saveContactButtonClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String id = idEditText.getText().toString(); 
			String firstName = firstNameEditText.getText().toString();
			String lastName = lastNameEditText.getText().toString();
			String email = emailEditText.getText().toString();
			
			// Make sure that the Id, FirstName, and LastName are filled in.
			// Id is set by the system and is ReadOnly.
			if (id.length() > 0 && firstName.length() > 0 && lastName.length() > 0) {
				Contact updateContact = new Contact(id, firstName, lastName, email);
				new SaveContactAsyncTask(updateContact, ContactAddEditActivity.this, new ContactSavedListener()).execute(updateContact);
			} 
			else {
				// create a new AlertDialog Builder for missing values
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ContactAddEditActivity.this);
				String errorMessage = "";
				if (firstName.length() == 0) {
					errorMessage = getString(R.string.errorFirstNameMessage);
				}
				else if (lastName.length() == 0){
					errorMessage = getString(R.string.errorLastNameMessage);
				}
				builder.setMessage(errorMessage);
				builder.setPositiveButton(R.string.errorButton, null);
				builder.show(); 
			} 
		} 
	}; 

	// ************************************************************************************
	// Save Contact on a separate thread.
	// ====================================================================================
	// Note: ContactService knows that a zero Id means a new Contact and that a non-zero
	//		Id means an existing Contact.
	// ************************************************************************************
	public interface ContactSavedListenerInterface {
		public void onContactSaved(Integer statusCode, String saveType);
	}
	
	public class SaveContactAsyncTask extends AsyncTask<Object, Object, Integer> {
		private ContactSavedListener contactSavedListener;
		private Context taskContext;
		HttpResponse response;
		private String saveType;


		// public constructor
		public SaveContactAsyncTask(Contact updateContact, Context context,	ContactSavedListener listener) {
			this.contactSavedListener = listener;
			this.taskContext = context;
		} 

		@Override
		protected Integer doInBackground(Object... arg0) {
			Contact updateContact = (Contact) arg0[0];
	    	ContactHttpClient contactHttpClient = new ContactHttpClient(this.taskContext);
	    	saveType = Integer.parseInt(updateContact.getId()) == 0 ? "Add" : "Update";
	    	return contactHttpClient.SaveContact(updateContact);
		}
		
		// executed back on the UI thread after the contact deleted
		protected void onPostExecute(Integer statusCode) {
			contactSavedListener.onContactSaved(statusCode, saveType);
		} 
	}	

	// listens for contact saved in background task
	private class ContactSavedListener implements ContactSavedListenerInterface {

		// create a new CityNameLocationLoadedListener
		public ContactSavedListener() {
		} // end CityNameLocationLoadedListener

		@Override
		public void onContactSaved(Integer statusCode, String saveType) {
			if (statusCode == 200){
				Toast message = Toast.makeText(ContactAddEditActivity.this, saveType == "Add" ? R.string.contactAdded : R.string.contactUpdated, Toast.LENGTH_SHORT);
				message.setGravity(Gravity.CENTER, message.getXOffset(), message.getYOffset());
				message.show();
			}
			else {
				Toast message = Toast.makeText(ContactAddEditActivity.this,  saveType == "Add" ? R.string.contactAddedErrorMessage : R.string.contactUpdatedErrorMessage, Toast.LENGTH_SHORT);
				message.setGravity(Gravity.CENTER, message.getXOffset(), message.getYOffset());
				message.show();
			}
			
	        // return to the Contact List
			Intent getContacts = new Intent(ContactAddEditActivity.this, ContactListActivity.class);
			getContacts.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(getContacts); 
		} 
	} 

}
