using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using ContactBusinessEntities;
using ContactDataAccess;

namespace ContactBusinessLogic
{
   public class ContactService : ServiceBase, IContactService
   {

      #region Constructors

      private IContactRepository _repository;


      public ContactService()
         : this(new ContactRepository())
      {
      }

      public ContactService(IContactRepository repository)
      {
         this._repository = repository;
      }

      #endregion

      public List<ContactView> GetContactViewList()
      {
         List<ContactView> contactViewList = new List<ContactView>();

         IEnumerable<Contact> contactList = _repository.GetContactList.OrderBy(c => c.LastName);
         foreach (Contact contact in contactList)
         {
            contactViewList.Add(GetContactView(contact.Id));
         }

         return contactViewList;
      }

      public ContactView GetContactView(int Id)
      {
         ContactView contactView = new ContactView();

         Contact contact = _repository.Get(Id);
         contactView = CreateContactView(contact);

         return contactView;
      }

      private ContactView CreateContactView(Contact contact)
      {
         ContactView contactView = new ContactView();
         contactView.Id = contact.Id;
         contactView.FirstName = contact.FirstName;
         contactView.LastName = contact.LastName;
         contactView.Email = contact.Email;
         return contactView;
      }

      public static Contact GetModel()
      {
         var entity = new Contact();
         return entity;
      }

      public int SaveContact(Contact entity)
      {
         int result = 0;

         // Validation
         this.Errors.Clear();

         try
         {
            // Data access
            if (!this.HasErrors)
            {
               // Save
               result = this._repository.Set(entity);
            }
         }
         catch (System.Data.UpdateException ex)
         {
            if (ex.InnerException != null && ex.InnerException is System.Data.SqlClient.SqlException
               && ((System.Data.SqlClient.SqlException)ex.InnerException).ErrorCode == 8152)
               Errors.Add(ResourceStrings.ErrorMaxLength);
            else
               Errors.Add(ex.Message);
         }

         return result;
      }

      public int DeleteContact(int id)
      {
         int result = 0;

         // Validation
         this.Errors.Clear();

         try
         {
            // Data access
            if (!this.HasErrors)
            {
               // Save
               result = this._repository.DeleteContact(id);
            }
         }
         catch (System.Data.UpdateException ex)
         {
            if (ex.InnerException != null && ex.InnerException is System.Data.SqlClient.SqlException
               && ((System.Data.SqlClient.SqlException)ex.InnerException).ErrorCode == 8152)
               Errors.Add(ResourceStrings.ErrorMaxLength);
            else
                Errors.Add(ex.Message);
         }

         return result;
      }
   }
}
