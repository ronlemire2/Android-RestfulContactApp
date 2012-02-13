using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using ContactBusinessEntities;

namespace ContactDataAccess
{
   public interface IContactRepository
   {
      IEnumerable<Contact> GetContactList { get; }
      Contact Get(int Id);
      int Set(Contact entity);
      int DeleteContact(int id);
   }
}
