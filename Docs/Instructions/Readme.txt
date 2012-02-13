===============================================================================
1. RestfulContactApp - Overview
===============================================================================
RestfulContactApp is a simplified but complete Restful CRUD application whose
purpose is to demonstrate an Android client communicating with a Restful 
Web Service to perform basic Create, Read, Update and Delete functions against
a single table database.

It is composed of 2 main projects:
	1) ContactService (Windows C# .NET)
		a) SQL Server 2008 database containing a single Contact table.
		b) DataAccess Layer that uses EntityFramework 4 as an Object Relational
			Mapper (ORM) to convert the database table into a C# .NET object.
		c) BusinessLogic Layer which is used by the Web Service facade to 
			access the Contacts.
		d) C# WCF Rest Web Service that is the interface between the Android 
			client and the database backend.
		e) The Web Service was created with Visual Studio 2010 and was hosted
			in IIS 7 on a Windows 7 machine.
	2) ContactClient (Android)
		a) 4 Activities to present the Contact data.
		b) HttpClient that uses HttpGet and HttpPut objects to perform CRUD.
		
===============================================================================
2. SQL Server 2008 Database script 
(Contact.bak located ContactServer/data folder)
===============================================================================		
USE [Contact]
GO
/****** Object:  Table [dbo].[Contact]    Script Date: 02/12/2012 14:01:26 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Contact](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[FirstName] [nvarchar](50) NOT NULL,
	[LastName] [nvarchar](50) NOT NULL,
	[Email] [nvarchar](50) NULL,
 CONSTRAINT [PK_Contact] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
		
===============================================================================
3. WCF Rest Web Service Interface Definition
===============================================================================
[ServiceContract]
public interface IContactWebService
{
  [OperationContract]
  [WebGet(UriTemplate = "/ContactList", ResponseFormat = WebMessageFormat.Json)]
  ContactList GetContactList();

  [OperationContract]
  [WebGet(UriTemplate = "/Contact/{Id}", ResponseFormat = WebMessageFormat.Json)]
  ContactList GetContact(string Id);

  [OperationContract]
  [WebInvoke(UriTemplate = "/Contact/{Id}", Method = "PUT", RequestFormat = WebMessageFormat.Json, ResponseFormat = WebMessageFormat.Json)]
  int SaveContact(string ID, Contact addEditContact);

  [OperationContract]
  [WebInvoke(UriTemplate = "/Contact/{Id}", Method = "DELETE", ResponseFormat = WebMessageFormat.Json)]
  int DeleteContact(string ID);
}

===============================================================================
4. WCF Data Contracts (Messages are in JSON format)
===============================================================================
[DataContract(Namespace = "")]
public class Contact
{
  [DataMember(Order=1)]
  public int Id;
  [DataMember(Order = 2)]
  public string FirstName;
  [DataMember(Order = 3)]
  public string LastName;
  [DataMember(Order = 4)]
  public string Email;
}

[CollectionDataContract(Name = "ContactList", Namespace = "")]
public class ContactList : List<Contact>
{
}

===============================================================================
5. Sample REST URLs
===============================================================================
1) HttpGet (all contacts) 		http://192.168.1.64:8085/service.svc/ContactList
2) HttpGet (one contact)		http://192.168.1.64:8085/service.svc/Contact/1
3) HttpPut (new contact)		http://192.168.1.64:8085/service.svc/Contact/0
4) HttpPut (update contact)		http://192.168.1.64:8085/service.svc/Contact/1
5) HttpDelete (delete contact)		http://192.168.1.64:8085/service.svc/Contact/1	

===============================================================================
6. Android Client quick how to use
===============================================================================
a) App starts by forcing user to enter REST URLs into SharedPreferences. There 
	are	2 URLs to be set. One called MULTIPLE_CONTACT_URL that is used to get 
	a collection of Contacts. One called SINGLE_CONTACT_URL that is used to 
	get, add, update, delete a single Contact. 
b) After setting the URLs the app will present the ContactList which will be 
	empty since the database starts with no contacts. The ContactList is a 
	TableLayout with each TableRow containing 3 columns: Id, FirstName, 
	LastName. Email is not presented in the ContactList. The Id column value
	is underlined. Click the Id column value to get to an exiting Contact.
c) To Add a new Contact:
	1) From the ContactList press the Menu icon.
	2) Press the 'Add Contact' menu option.
	3) The ContactAddEditActivity will be started.
	4) App automatically enters zero(0) for Id. Enter 2 required 
		fields (FirstName, LastName) and optional Email field then press 
		'Add Contact' button.
	5) App will confirm with 'Contact Added' toast.
d) To Update an existing Contact:
	1) From the ContactList click on the underlined Id column.
	2) The ContactViewActivity will be started. In the view the Contact
		is read-only.
	3) Press the Menu icon and click on the 'Update Contact' menu option
	4) App automatically enters the Id selected from ContactList. Change 
		FirstName, LastName, Email fields then press 'Update Contact' 
		button. 
	5) App will confirm with 'Contact Updated' toast.
e) To Delete an existing Contact:
	1) From the ContactList click on the underlined Id column.
	2) The ContactViewActivity will be started. In the view the Contact
		is read-only.
	3) Press the Menu icon and click on the 'Delete Contact' menu option.
	4) There is no ContactDeleteActivity.
	5) An AlertDialog will be presented with 'Are you sure?'
	6) Press 'Yes' button.
	7) App will confirm with 'Contact Deleted' toast.
e) To set URLs after being set originall:
	1) MULTIPLE_CONTACT_URL for getting all contacts
	2) SINGLE_CONTACT_URL for getting, adding, updating, deleting a single
		contact
		
===============================================================================
7. Android Client screenshots
===============================================================================
Located in Docs/Screenshots will step through the basic uses cases. The .png
file names go in sequential order:
	1) Set Preferences (note that app requires URLs to be entered before 
		presenting any activities).
	2) ContactList
	3) Add Contact
	4) Update Contact
	5) Delete Contact
	
===============================================================================
8. ContactHttpClient class
===============================================================================
ContactHttpClient contains all the Android rest calls to ContactServer:
	1) public Contact[] GetContactArray(String Id) - pass in zero(0) to
		get all contacts or a number > 0 to get a single contact.
	2) public Integer SaveContact(Contact saveContact) - pass in a Contact
		with Id set to zero(0) to add new contact or pass in a Contact with
		Id > 0 to update and existing contact.
	3) public Integer DeleteContact(Contact deleteContact) - pass in a 
		Contact with Id > 0 to delete and existing contact.
		
===============================================================================
9. Android Client Testing
===============================================================================
a) Real3D emulator 		(Android Version 2.2 	- API 8 	- Froyo)
b) DroidRAZR emulator 	(Android Version 2.3.3 	- API 10 	- Gingerbread)
c) Galaxy Nexus device	(Android Version 4.0	- API 14	- Ice Cream Sandwich)