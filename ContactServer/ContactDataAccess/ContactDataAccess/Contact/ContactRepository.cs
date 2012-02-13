using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using ContactBusinessEntities;

namespace ContactDataAccess
{
   public class ContactRepository : RepositoryBase, IContactRepository
   {
      public IEnumerable<Contact> GetContactList
      {
         get
         {
            var entityList = default(IEnumerable<Contact>);

            // Calling data access (entity framework)
            entityList = _dbContext.Contacts;

            return entityList;
         }
      }

      public Contact Get(int Id)
      {
         var result = default(Contact);

         // Calling data access (entity framework)
         result = _dbContext.Contacts
            .Where(c => c.Id == Id).FirstOrDefault();

         return result;
      }

      public int Set(Contact entity)
      {
         var result = 0;

         // Load object into context (entity framework) 
         var loadedEntity = _dbContext.Contacts.Where(c => c.Id== entity.Id).FirstOrDefault();

         // Modify the context
         if (loadedEntity == null)
         { //not found?
            // Add
            this._dbContext.Contacts.AddObject(entity);
         }
         else
         {
            // Update
            this._dbContext.Contacts.ApplyCurrentValues(entity);
         }

         // Save in data access (entity framework)
         result = this._dbContext.SaveChanges();

         return result;
      }

      public int DeleteContact(int Id)
      {
         int result = 0;

         // Calling data access (entity framework)
         var contact = Get(Id);
         _dbContext.DeleteObject(contact);
         // Save in data access (entity framework)
         result = this._dbContext.SaveChanges();

         return result;
      }

   }

}
