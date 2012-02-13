using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.ServiceModel.Channels;
using System.ServiceModel.Activation;
using System.ServiceModel.Description;
using ContactBusinessLogic;
using ContactBusinessEntities;

namespace ContactWebService
{
   [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single)]
   [AspNetCompatibilityRequirements(RequirementsMode = AspNetCompatibilityRequirementsMode.Allowed)]
   public class ContactWebService : IContactWebService
   {
      // BusinessLogic.ContactService
      IContactService _contactService = new ContactService();
      
      public ContactList GetContactList()
      {
         List<ContactView> contactViewList = _contactService.GetContactViewList();
         ContactList contactList = new ContactList();

         foreach (ContactView contactView in contactViewList)
         {
            contactList.Add(new Contact() { Id = contactView.Id, FirstName = contactView.FirstName.Trim(), LastName = contactView.LastName.Trim(), Email = contactView.Email.Trim() });
         }

         return contactList;
      }

      public ContactList GetContact(string Id)
      {
         ContactList contactList = new ContactList();
         ContactView contactView = null;
         Contact contact = new Contact();

         contactView = _contactService.GetContactView(int.Parse(Id));
         contact.Id = contactView.Id;
         contact.FirstName = contactView.FirstName.Trim();
         contact.LastName = contactView.LastName.Trim();
         contact.Email = contactView.Email.Trim();
         contactList.Add(contact);

         return contactList;
      }

      public int SaveContact(string Id, Contact addEditContact)
      {
         int result = 0;
         ContactBusinessEntities.Contact contact = new ContactBusinessEntities.Contact();
         contact.Id = addEditContact.Id;
         contact.FirstName = addEditContact.FirstName;
         contact.LastName = addEditContact.LastName;
         contact.Email = addEditContact.Email;
         result = _contactService.SaveContact(contact);
         return result;
      }

      public int DeleteContact(string Id)
      {
         int result = 0;
         result = _contactService.DeleteContact(int.Parse(Id));
         return result;
      }
   }
}
