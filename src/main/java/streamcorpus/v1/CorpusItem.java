/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package streamcorpus.v1;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CorpusItem is the thrift equivalent of
 * http://trec-kba.org/schemas/v1.0/corpus-item.json
 */
public class CorpusItem implements org.apache.thrift.TBase<CorpusItem, CorpusItem._Fields>, java.io.Serializable, Cloneable, Comparable<CorpusItem> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("CorpusItem");

  private static final org.apache.thrift.protocol.TField DOC_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("doc_id", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField ABS_URL_FIELD_DESC = new org.apache.thrift.protocol.TField("abs_url", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField SCHOST_FIELD_DESC = new org.apache.thrift.protocol.TField("schost", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField ORIGINAL_URL_FIELD_DESC = new org.apache.thrift.protocol.TField("original_url", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField SOURCE_FIELD_DESC = new org.apache.thrift.protocol.TField("source", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField TITLE_FIELD_DESC = new org.apache.thrift.protocol.TField("title", org.apache.thrift.protocol.TType.STRUCT, (short)6);
  private static final org.apache.thrift.protocol.TField BODY_FIELD_DESC = new org.apache.thrift.protocol.TField("body", org.apache.thrift.protocol.TType.STRUCT, (short)7);
  private static final org.apache.thrift.protocol.TField ANCHOR_FIELD_DESC = new org.apache.thrift.protocol.TField("anchor", org.apache.thrift.protocol.TType.STRUCT, (short)8);
  private static final org.apache.thrift.protocol.TField SOURCE_METADATA_FIELD_DESC = new org.apache.thrift.protocol.TField("source_metadata", org.apache.thrift.protocol.TType.STRING, (short)9);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new CorpusItemStandardSchemeFactory());
    schemes.put(TupleScheme.class, new CorpusItemTupleSchemeFactory());
  }

  public String doc_id; // required
  public ByteBuffer abs_url; // required
  public String schost; // required
  public ByteBuffer original_url; // required
  public String source; // required
  public ContentItem title; // required
  public ContentItem body; // required
  public ContentItem anchor; // required
  public ByteBuffer source_metadata; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    DOC_ID((short)1, "doc_id"),
    ABS_URL((short)2, "abs_url"),
    SCHOST((short)3, "schost"),
    ORIGINAL_URL((short)4, "original_url"),
    SOURCE((short)5, "source"),
    TITLE((short)6, "title"),
    BODY((short)7, "body"),
    ANCHOR((short)8, "anchor"),
    SOURCE_METADATA((short)9, "source_metadata");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // DOC_ID
          return DOC_ID;
        case 2: // ABS_URL
          return ABS_URL;
        case 3: // SCHOST
          return SCHOST;
        case 4: // ORIGINAL_URL
          return ORIGINAL_URL;
        case 5: // SOURCE
          return SOURCE;
        case 6: // TITLE
          return TITLE;
        case 7: // BODY
          return BODY;
        case 8: // ANCHOR
          return ANCHOR;
        case 9: // SOURCE_METADATA
          return SOURCE_METADATA;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.DOC_ID, new org.apache.thrift.meta_data.FieldMetaData("doc_id", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ABS_URL, new org.apache.thrift.meta_data.FieldMetaData("abs_url", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    tmpMap.put(_Fields.SCHOST, new org.apache.thrift.meta_data.FieldMetaData("schost", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ORIGINAL_URL, new org.apache.thrift.meta_data.FieldMetaData("original_url", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    tmpMap.put(_Fields.SOURCE, new org.apache.thrift.meta_data.FieldMetaData("source", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TITLE, new org.apache.thrift.meta_data.FieldMetaData("title", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ContentItem.class)));
    tmpMap.put(_Fields.BODY, new org.apache.thrift.meta_data.FieldMetaData("body", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ContentItem.class)));
    tmpMap.put(_Fields.ANCHOR, new org.apache.thrift.meta_data.FieldMetaData("anchor", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ContentItem.class)));
    tmpMap.put(_Fields.SOURCE_METADATA, new org.apache.thrift.meta_data.FieldMetaData("source_metadata", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , "SourceMetadata")));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(CorpusItem.class, metaDataMap);
  }

  public CorpusItem() {
  }

  public CorpusItem(
    String doc_id,
    ByteBuffer abs_url,
    String schost,
    ByteBuffer original_url,
    String source,
    ContentItem title,
    ContentItem body,
    ContentItem anchor,
    ByteBuffer source_metadata)
  {
    this();
    this.doc_id = doc_id;
    this.abs_url = abs_url;
    this.schost = schost;
    this.original_url = original_url;
    this.source = source;
    this.title = title;
    this.body = body;
    this.anchor = anchor;
    this.source_metadata = source_metadata;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CorpusItem(CorpusItem other) {
    if (other.isSetDoc_id()) {
      this.doc_id = other.doc_id;
    }
    if (other.isSetAbs_url()) {
      this.abs_url = org.apache.thrift.TBaseHelper.copyBinary(other.abs_url);
;
    }
    if (other.isSetSchost()) {
      this.schost = other.schost;
    }
    if (other.isSetOriginal_url()) {
      this.original_url = org.apache.thrift.TBaseHelper.copyBinary(other.original_url);
;
    }
    if (other.isSetSource()) {
      this.source = other.source;
    }
    if (other.isSetTitle()) {
      this.title = new ContentItem(other.title);
    }
    if (other.isSetBody()) {
      this.body = new ContentItem(other.body);
    }
    if (other.isSetAnchor()) {
      this.anchor = new ContentItem(other.anchor);
    }
    if (other.isSetSource_metadata()) {
      this.source_metadata = other.source_metadata;
    }
  }

  public CorpusItem deepCopy() {
    return new CorpusItem(this);
  }

  @Override
  public void clear() {
    this.doc_id = null;
    this.abs_url = null;
    this.schost = null;
    this.original_url = null;
    this.source = null;
    this.title = null;
    this.body = null;
    this.anchor = null;
    this.source_metadata = null;
  }

  public String getDoc_id() {
    return this.doc_id;
  }

  public CorpusItem setDoc_id(String doc_id) {
    this.doc_id = doc_id;
    return this;
  }

  public void unsetDoc_id() {
    this.doc_id = null;
  }

  /** Returns true if field doc_id is set (has been assigned a value) and false otherwise */
  public boolean isSetDoc_id() {
    return this.doc_id != null;
  }

  public void setDoc_idIsSet(boolean value) {
    if (!value) {
      this.doc_id = null;
    }
  }

  public byte[] getAbs_url() {
    setAbs_url(org.apache.thrift.TBaseHelper.rightSize(abs_url));
    return abs_url == null ? null : abs_url.array();
  }

  public ByteBuffer bufferForAbs_url() {
    return abs_url;
  }

  public CorpusItem setAbs_url(byte[] abs_url) {
    setAbs_url(abs_url == null ? (ByteBuffer)null : ByteBuffer.wrap(abs_url));
    return this;
  }

  public CorpusItem setAbs_url(ByteBuffer abs_url) {
    this.abs_url = abs_url;
    return this;
  }

  public void unsetAbs_url() {
    this.abs_url = null;
  }

  /** Returns true if field abs_url is set (has been assigned a value) and false otherwise */
  public boolean isSetAbs_url() {
    return this.abs_url != null;
  }

  public void setAbs_urlIsSet(boolean value) {
    if (!value) {
      this.abs_url = null;
    }
  }

  public String getSchost() {
    return this.schost;
  }

  public CorpusItem setSchost(String schost) {
    this.schost = schost;
    return this;
  }

  public void unsetSchost() {
    this.schost = null;
  }

  /** Returns true if field schost is set (has been assigned a value) and false otherwise */
  public boolean isSetSchost() {
    return this.schost != null;
  }

  public void setSchostIsSet(boolean value) {
    if (!value) {
      this.schost = null;
    }
  }

  public byte[] getOriginal_url() {
    setOriginal_url(org.apache.thrift.TBaseHelper.rightSize(original_url));
    return original_url == null ? null : original_url.array();
  }

  public ByteBuffer bufferForOriginal_url() {
    return original_url;
  }

  public CorpusItem setOriginal_url(byte[] original_url) {
    setOriginal_url(original_url == null ? (ByteBuffer)null : ByteBuffer.wrap(original_url));
    return this;
  }

  public CorpusItem setOriginal_url(ByteBuffer original_url) {
    this.original_url = original_url;
    return this;
  }

  public void unsetOriginal_url() {
    this.original_url = null;
  }

  /** Returns true if field original_url is set (has been assigned a value) and false otherwise */
  public boolean isSetOriginal_url() {
    return this.original_url != null;
  }

  public void setOriginal_urlIsSet(boolean value) {
    if (!value) {
      this.original_url = null;
    }
  }

  public String getSource() {
    return this.source;
  }

  public CorpusItem setSource(String source) {
    this.source = source;
    return this;
  }

  public void unsetSource() {
    this.source = null;
  }

  /** Returns true if field source is set (has been assigned a value) and false otherwise */
  public boolean isSetSource() {
    return this.source != null;
  }

  public void setSourceIsSet(boolean value) {
    if (!value) {
      this.source = null;
    }
  }

  public ContentItem getTitle() {
    return this.title;
  }

  public CorpusItem setTitle(ContentItem title) {
    this.title = title;
    return this;
  }

  public void unsetTitle() {
    this.title = null;
  }

  /** Returns true if field title is set (has been assigned a value) and false otherwise */
  public boolean isSetTitle() {
    return this.title != null;
  }

  public void setTitleIsSet(boolean value) {
    if (!value) {
      this.title = null;
    }
  }

  public ContentItem getBody() {
    return this.body;
  }

  public CorpusItem setBody(ContentItem body) {
    this.body = body;
    return this;
  }

  public void unsetBody() {
    this.body = null;
  }

  /** Returns true if field body is set (has been assigned a value) and false otherwise */
  public boolean isSetBody() {
    return this.body != null;
  }

  public void setBodyIsSet(boolean value) {
    if (!value) {
      this.body = null;
    }
  }

  public ContentItem getAnchor() {
    return this.anchor;
  }

  public CorpusItem setAnchor(ContentItem anchor) {
    this.anchor = anchor;
    return this;
  }

  public void unsetAnchor() {
    this.anchor = null;
  }

  /** Returns true if field anchor is set (has been assigned a value) and false otherwise */
  public boolean isSetAnchor() {
    return this.anchor != null;
  }

  public void setAnchorIsSet(boolean value) {
    if (!value) {
      this.anchor = null;
    }
  }

  public byte[] getSource_metadata() {
    setSource_metadata(org.apache.thrift.TBaseHelper.rightSize(source_metadata));
    return source_metadata == null ? null : source_metadata.array();
  }

  public ByteBuffer bufferForSource_metadata() {
    return source_metadata;
  }

  public CorpusItem setSource_metadata(byte[] source_metadata) {
    setSource_metadata(source_metadata == null ? (ByteBuffer)null : ByteBuffer.wrap(source_metadata));
    return this;
  }

  public CorpusItem setSource_metadata(ByteBuffer source_metadata) {
    this.source_metadata = source_metadata;
    return this;
  }

  public void unsetSource_metadata() {
    this.source_metadata = null;
  }

  /** Returns true if field source_metadata is set (has been assigned a value) and false otherwise */
  public boolean isSetSource_metadata() {
    return this.source_metadata != null;
  }

  public void setSource_metadataIsSet(boolean value) {
    if (!value) {
      this.source_metadata = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case DOC_ID:
      if (value == null) {
        unsetDoc_id();
      } else {
        setDoc_id((String)value);
      }
      break;

    case ABS_URL:
      if (value == null) {
        unsetAbs_url();
      } else {
        setAbs_url((ByteBuffer)value);
      }
      break;

    case SCHOST:
      if (value == null) {
        unsetSchost();
      } else {
        setSchost((String)value);
      }
      break;

    case ORIGINAL_URL:
      if (value == null) {
        unsetOriginal_url();
      } else {
        setOriginal_url((ByteBuffer)value);
      }
      break;

    case SOURCE:
      if (value == null) {
        unsetSource();
      } else {
        setSource((String)value);
      }
      break;

    case TITLE:
      if (value == null) {
        unsetTitle();
      } else {
        setTitle((ContentItem)value);
      }
      break;

    case BODY:
      if (value == null) {
        unsetBody();
      } else {
        setBody((ContentItem)value);
      }
      break;

    case ANCHOR:
      if (value == null) {
        unsetAnchor();
      } else {
        setAnchor((ContentItem)value);
      }
      break;

    case SOURCE_METADATA:
      if (value == null) {
        unsetSource_metadata();
      } else {
        setSource_metadata((ByteBuffer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case DOC_ID:
      return getDoc_id();

    case ABS_URL:
      return getAbs_url();

    case SCHOST:
      return getSchost();

    case ORIGINAL_URL:
      return getOriginal_url();

    case SOURCE:
      return getSource();

    case TITLE:
      return getTitle();

    case BODY:
      return getBody();

    case ANCHOR:
      return getAnchor();

    case SOURCE_METADATA:
      return getSource_metadata();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case DOC_ID:
      return isSetDoc_id();
    case ABS_URL:
      return isSetAbs_url();
    case SCHOST:
      return isSetSchost();
    case ORIGINAL_URL:
      return isSetOriginal_url();
    case SOURCE:
      return isSetSource();
    case TITLE:
      return isSetTitle();
    case BODY:
      return isSetBody();
    case ANCHOR:
      return isSetAnchor();
    case SOURCE_METADATA:
      return isSetSource_metadata();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof CorpusItem)
      return this.equals((CorpusItem)that);
    return false;
  }

  public boolean equals(CorpusItem that) {
    if (that == null)
      return false;

    boolean this_present_doc_id = true && this.isSetDoc_id();
    boolean that_present_doc_id = true && that.isSetDoc_id();
    if (this_present_doc_id || that_present_doc_id) {
      if (!(this_present_doc_id && that_present_doc_id))
        return false;
      if (!this.doc_id.equals(that.doc_id))
        return false;
    }

    boolean this_present_abs_url = true && this.isSetAbs_url();
    boolean that_present_abs_url = true && that.isSetAbs_url();
    if (this_present_abs_url || that_present_abs_url) {
      if (!(this_present_abs_url && that_present_abs_url))
        return false;
      if (!this.abs_url.equals(that.abs_url))
        return false;
    }

    boolean this_present_schost = true && this.isSetSchost();
    boolean that_present_schost = true && that.isSetSchost();
    if (this_present_schost || that_present_schost) {
      if (!(this_present_schost && that_present_schost))
        return false;
      if (!this.schost.equals(that.schost))
        return false;
    }

    boolean this_present_original_url = true && this.isSetOriginal_url();
    boolean that_present_original_url = true && that.isSetOriginal_url();
    if (this_present_original_url || that_present_original_url) {
      if (!(this_present_original_url && that_present_original_url))
        return false;
      if (!this.original_url.equals(that.original_url))
        return false;
    }

    boolean this_present_source = true && this.isSetSource();
    boolean that_present_source = true && that.isSetSource();
    if (this_present_source || that_present_source) {
      if (!(this_present_source && that_present_source))
        return false;
      if (!this.source.equals(that.source))
        return false;
    }

    boolean this_present_title = true && this.isSetTitle();
    boolean that_present_title = true && that.isSetTitle();
    if (this_present_title || that_present_title) {
      if (!(this_present_title && that_present_title))
        return false;
      if (!this.title.equals(that.title))
        return false;
    }

    boolean this_present_body = true && this.isSetBody();
    boolean that_present_body = true && that.isSetBody();
    if (this_present_body || that_present_body) {
      if (!(this_present_body && that_present_body))
        return false;
      if (!this.body.equals(that.body))
        return false;
    }

    boolean this_present_anchor = true && this.isSetAnchor();
    boolean that_present_anchor = true && that.isSetAnchor();
    if (this_present_anchor || that_present_anchor) {
      if (!(this_present_anchor && that_present_anchor))
        return false;
      if (!this.anchor.equals(that.anchor))
        return false;
    }

    boolean this_present_source_metadata = true && this.isSetSource_metadata();
    boolean that_present_source_metadata = true && that.isSetSource_metadata();
    if (this_present_source_metadata || that_present_source_metadata) {
      if (!(this_present_source_metadata && that_present_source_metadata))
        return false;
      if (!this.source_metadata.equals(that.source_metadata))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(CorpusItem other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetDoc_id()).compareTo(other.isSetDoc_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDoc_id()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.doc_id, other.doc_id);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAbs_url()).compareTo(other.isSetAbs_url());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAbs_url()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.abs_url, other.abs_url);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetSchost()).compareTo(other.isSetSchost());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSchost()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.schost, other.schost);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetOriginal_url()).compareTo(other.isSetOriginal_url());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetOriginal_url()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.original_url, other.original_url);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetSource()).compareTo(other.isSetSource());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSource()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.source, other.source);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTitle()).compareTo(other.isSetTitle());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTitle()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.title, other.title);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetBody()).compareTo(other.isSetBody());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetBody()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.body, other.body);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAnchor()).compareTo(other.isSetAnchor());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAnchor()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.anchor, other.anchor);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetSource_metadata()).compareTo(other.isSetSource_metadata());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSource_metadata()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.source_metadata, other.source_metadata);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("CorpusItem(");
    boolean first = true;

    sb.append("doc_id:");
    if (this.doc_id == null) {
      sb.append("null");
    } else {
      sb.append(this.doc_id);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("abs_url:");
    if (this.abs_url == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.abs_url, sb);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("schost:");
    if (this.schost == null) {
      sb.append("null");
    } else {
      sb.append(this.schost);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("original_url:");
    if (this.original_url == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.original_url, sb);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("source:");
    if (this.source == null) {
      sb.append("null");
    } else {
      sb.append(this.source);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("title:");
    if (this.title == null) {
      sb.append("null");
    } else {
      sb.append(this.title);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("body:");
    if (this.body == null) {
      sb.append("null");
    } else {
      sb.append(this.body);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("anchor:");
    if (this.anchor == null) {
      sb.append("null");
    } else {
      sb.append(this.anchor);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("source_metadata:");
    if (this.source_metadata == null) {
      sb.append("null");
    } else {
      sb.append(this.source_metadata);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
    if (title != null) {
      title.validate();
    }
    if (body != null) {
      body.validate();
    }
    if (anchor != null) {
      anchor.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class CorpusItemStandardSchemeFactory implements SchemeFactory {
    public CorpusItemStandardScheme getScheme() {
      return new CorpusItemStandardScheme();
    }
  }

  private static class CorpusItemStandardScheme extends StandardScheme<CorpusItem> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, CorpusItem struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // DOC_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.doc_id = iprot.readString();
              struct.setDoc_idIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // ABS_URL
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.abs_url = iprot.readBinary();
              struct.setAbs_urlIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // SCHOST
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.schost = iprot.readString();
              struct.setSchostIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // ORIGINAL_URL
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.original_url = iprot.readBinary();
              struct.setOriginal_urlIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // SOURCE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.source = iprot.readString();
              struct.setSourceIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // TITLE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.title = new ContentItem();
              struct.title.read(iprot);
              struct.setTitleIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // BODY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.body = new ContentItem();
              struct.body.read(iprot);
              struct.setBodyIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 8: // ANCHOR
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.anchor = new ContentItem();
              struct.anchor.read(iprot);
              struct.setAnchorIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 9: // SOURCE_METADATA
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.source_metadata = iprot.readBinary();
              struct.setSource_metadataIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, CorpusItem struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.doc_id != null) {
        oprot.writeFieldBegin(DOC_ID_FIELD_DESC);
        oprot.writeString(struct.doc_id);
        oprot.writeFieldEnd();
      }
      if (struct.abs_url != null) {
        oprot.writeFieldBegin(ABS_URL_FIELD_DESC);
        oprot.writeBinary(struct.abs_url);
        oprot.writeFieldEnd();
      }
      if (struct.schost != null) {
        oprot.writeFieldBegin(SCHOST_FIELD_DESC);
        oprot.writeString(struct.schost);
        oprot.writeFieldEnd();
      }
      if (struct.original_url != null) {
        oprot.writeFieldBegin(ORIGINAL_URL_FIELD_DESC);
        oprot.writeBinary(struct.original_url);
        oprot.writeFieldEnd();
      }
      if (struct.source != null) {
        oprot.writeFieldBegin(SOURCE_FIELD_DESC);
        oprot.writeString(struct.source);
        oprot.writeFieldEnd();
      }
      if (struct.title != null) {
        oprot.writeFieldBegin(TITLE_FIELD_DESC);
        struct.title.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.body != null) {
        oprot.writeFieldBegin(BODY_FIELD_DESC);
        struct.body.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.anchor != null) {
        oprot.writeFieldBegin(ANCHOR_FIELD_DESC);
        struct.anchor.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.source_metadata != null) {
        oprot.writeFieldBegin(SOURCE_METADATA_FIELD_DESC);
        oprot.writeBinary(struct.source_metadata);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CorpusItemTupleSchemeFactory implements SchemeFactory {
    public CorpusItemTupleScheme getScheme() {
      return new CorpusItemTupleScheme();
    }
  }

  private static class CorpusItemTupleScheme extends TupleScheme<CorpusItem> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, CorpusItem struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetDoc_id()) {
        optionals.set(0);
      }
      if (struct.isSetAbs_url()) {
        optionals.set(1);
      }
      if (struct.isSetSchost()) {
        optionals.set(2);
      }
      if (struct.isSetOriginal_url()) {
        optionals.set(3);
      }
      if (struct.isSetSource()) {
        optionals.set(4);
      }
      if (struct.isSetTitle()) {
        optionals.set(5);
      }
      if (struct.isSetBody()) {
        optionals.set(6);
      }
      if (struct.isSetAnchor()) {
        optionals.set(7);
      }
      if (struct.isSetSource_metadata()) {
        optionals.set(8);
      }
      oprot.writeBitSet(optionals, 9);
      if (struct.isSetDoc_id()) {
        oprot.writeString(struct.doc_id);
      }
      if (struct.isSetAbs_url()) {
        oprot.writeBinary(struct.abs_url);
      }
      if (struct.isSetSchost()) {
        oprot.writeString(struct.schost);
      }
      if (struct.isSetOriginal_url()) {
        oprot.writeBinary(struct.original_url);
      }
      if (struct.isSetSource()) {
        oprot.writeString(struct.source);
      }
      if (struct.isSetTitle()) {
        struct.title.write(oprot);
      }
      if (struct.isSetBody()) {
        struct.body.write(oprot);
      }
      if (struct.isSetAnchor()) {
        struct.anchor.write(oprot);
      }
      if (struct.isSetSource_metadata()) {
        oprot.writeBinary(struct.source_metadata);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, CorpusItem struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(9);
      if (incoming.get(0)) {
        struct.doc_id = iprot.readString();
        struct.setDoc_idIsSet(true);
      }
      if (incoming.get(1)) {
        struct.abs_url = iprot.readBinary();
        struct.setAbs_urlIsSet(true);
      }
      if (incoming.get(2)) {
        struct.schost = iprot.readString();
        struct.setSchostIsSet(true);
      }
      if (incoming.get(3)) {
        struct.original_url = iprot.readBinary();
        struct.setOriginal_urlIsSet(true);
      }
      if (incoming.get(4)) {
        struct.source = iprot.readString();
        struct.setSourceIsSet(true);
      }
      if (incoming.get(5)) {
        struct.title = new ContentItem();
        struct.title.read(iprot);
        struct.setTitleIsSet(true);
      }
      if (incoming.get(6)) {
        struct.body = new ContentItem();
        struct.body.read(iprot);
        struct.setBodyIsSet(true);
      }
      if (incoming.get(7)) {
        struct.anchor = new ContentItem();
        struct.anchor.read(iprot);
        struct.setAnchorIsSet(true);
      }
      if (incoming.get(8)) {
        struct.source_metadata = iprot.readBinary();
        struct.setSource_metadataIsSet(true);
      }
    }
  }

}

