package pl.matsuo.interfacer.showcase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import pl.matsuo.interfacer.avro.BasicKeyValue;
import pl.matsuo.interfacer.avro.GenericDate;
import pl.matsuo.interfacer.avro.GenericShape;
import pl.matsuo.interfacer.avro.GenericString;
import pl.matsuo.interfacer.avro.KeyValueReference;
import pl.matsuo.interfacer.avro.NamedRecord;
import pl.matsuo.interfacer.avro.NoInterfacesObject;
import pl.matsuo.interfacer.avro.Person;
import pl.matsuo.interfacer.avro.Address;

public class TestGeneratedResources {

  @Test
  public void checkImplementedTypes() {
    assertTrue("IKeyValue is not implemented by BasicKeyValue!", IKeyValue.class.isAssignableFrom(BasicKeyValue.class));
    assertTrue("IKeyValueProvider is not implemented by KeyValueReference!", IKeyValueProvider.class.isAssignableFrom(KeyValueReference.class));
    assertTrue("HasKey is not implemented by KeyValueReference!", HasKey.class.isAssignableFrom(KeyValueReference.class));

    assertTrue("Entity is not implemented by Person!", Entity.class.isAssignableFrom(Person.class));
    assertTrue("Entity is not implemented by Address!", Entity.class.isAssignableFrom(Address.class));

    assertFalse("HasKey is not implemented by NoInterfacesObject!", HasKey.class.isAssignableFrom(NoInterfacesObject.class));
    assertFalse("HasValue is not implemented by NoInterfacesObject!", HasValue.class.isAssignableFrom(NoInterfacesObject.class));
    assertFalse("KeyValueReference is not implemented by NoInterfacesObject!", KeyValueReference.class.isAssignableFrom(NoInterfacesObject.class));
    assertFalse("IKeyValueProvider is not implemented by NoInterfacesObject!", IKeyValueProvider.class.isAssignableFrom(NoInterfacesObject.class));

    assertTrue("HasName is not implemented by NamedRecord!", HasName.class.isAssignableFrom(NamedRecord.class));
    assertTrue("MutableOwner is not implemented by NamedRecord!", MutableOwner.class.isAssignableFrom(NamedRecord.class));
    assertTrue("MutableOwner2 is not implemented by NamedRecord!", MutableOwner2.class.isAssignableFrom(NamedRecord.class));
    assertFalse("MutableOwner3 is not implemented by NamedRecord!", MutableOwner3.class.isAssignableFrom(NamedRecord.class));

    assertTrue("GenericInterface is not implemented by GenericString!", GenericInterface.class.isAssignableFrom(GenericString.class));
    assertTrue("GenericInterface is not implemented by GenericShape!", GenericInterface.class.isAssignableFrom(GenericShape.class));
    assertTrue("GenericInterface is not implemented by GenericDate!", GenericInterface.class.isAssignableFrom(GenericDate.class));
  }
}
