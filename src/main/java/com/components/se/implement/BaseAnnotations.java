package com.components.se.implement;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ByIdOrName;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.pagefactory.AbstractAnnotations;

import com.components.se.enums.Until;

public class BaseAnnotations extends AbstractAnnotations {

    private Field field;

    private Until syncType;

    /**
     * @param field expected to be an element in a Page Object
     */
    public BaseAnnotations(Field field) {
        this.field = field;
    }

    /**
     * @param syncType
     */
    public BaseAnnotations(Until syncType) {
        this.syncType = syncType;
    }

    /**
     * {@inheritDoc}
     * @return true if @CacheLookup annotation exists on a field
     */
    public boolean isLookupCached() {
        return (field.getAnnotation(CacheLookup.class) != null);
    }

    /**
     * {@inheritDoc}
     *
     * Looks for one of {@link org.openqa.selenium.support.FindBy},
     * {@link org.openqa.selenium.support.FindBys} or
     * {@link org.openqa.selenium.support.FindAll} field annotations. In case no
     * annotaions provided for field, uses field name as 'id' or 'name'.
     * @throws IllegalArgumentException when more than one annotation on a field provided
     */
    public By buildBy() {
        assertValidAnnotations();

        By ans = null;
        // FindBys findBys = field.getAnnotation(FindBys.class);
        // if (findBys != null) {
        // ans = buildByFromFindBys(findBys);
        // }
        //
        // FindAll findAll = field.getAnnotation(FindAll.class);
        // if (ans == null && findAll != null) {
        // ans = buildBysFromFindByOneOf(findAll);
        // }
        //
        // FindBy findBy = field.getAnnotation(FindBy.class);
        // if (ans == null && findBy != null) {
        // ans = buildByFromFindBy(findBy);
        // }

        CustomFindBy findby = field.getAnnotation(CustomFindBy.class);
        if (ans == null && findby != null) {
            ans = buildByFromFindBy(findby);
        }

        if (ans == null) {
            ans = buildByFromDefault();
        }

        if (ans == null) {
            throw new IllegalArgumentException("Cannot determine how to locate element " + field);
        }

        return ans;
    }

    /**
     * Get the name of via mentioned in @CustomFindBy else Default value
     * @return Until
     */
    public Until getSyncType() {
        if (field != null) {
            CustomFindBy cFindBy = field.getAnnotation(CustomFindBy.class);
            if (cFindBy != null) {
                return cFindBy.waitFor();
            }
            else {
                return Until.Present;
            }
        }
        else {
            if (syncType != null) {
                return syncType;
            }
            else {
                return Until.Present;

            }
        }
    }

    /**
     * Get timeout number, if not defined,
     * @return timeout
     */
    public int getTimeout() {
        if (field != null) {
            CustomFindBy cFindBy = field.getAnnotation(CustomFindBy.class);
            if (cFindBy != null) {
                return cFindBy.timeout();
            }
            else {
                return -1;
            }
        }
        else {
            return -1;
        }

    }

    protected Field getField() {
        return field;
    }

    /**
     * Constructs a By object via fieldName
     * @return By
     */
    protected By buildByFromDefault() {
        return new ByIdOrName(field.getName());
    }

    /**
     * Validate annotations FindBys,FindAll and FindBy if null
     */
    protected void assertValidAnnotations() {
        FindBys findBys = field.getAnnotation(FindBys.class);
        FindAll findAll = field.getAnnotation(FindAll.class);
        FindBy findBy = field.getAnnotation(FindBy.class);
        if (findBys != null && findBy != null) {
            throw new IllegalArgumentException(
                    "If you use a '@FindBys' annotation, " + "you must not also use a '@FindBy' annotation");
        }
        if (findAll != null && findBy != null) {
            throw new IllegalArgumentException(
                    "If you use a '@FindAll' annotation, " + "you must not also use a '@FindBy' annotation");
        }
        if (findAll != null && findBys != null) {
            throw new IllegalArgumentException(
                    "If you use a '@FindAll' annotation, " + "you must not also use a '@FindBys' annotation");
        }
    }

    /**
     * Constructs a By object via FindBy annotation
     * @param findBy CustomFindBy
     * @return By
     */
    private By buildByFromFindBy(CustomFindBy findBy) {
        assertValidFindBy(findBy);

        By ans = buildByFromShortFindBy(findBy);
        if (ans == null) {
            ans = buildByFromLongFindBy(findBy);
        }

        return ans;
    }

    /**
     * Constructs a By object via LongFindBy annotation
     * @param findBy CustomFindBy
     * @return By
     */
    private By buildByFromLongFindBy(CustomFindBy findBy) {
        How how = findBy.how();
        String using = findBy.using();

        switch (how) {
            case CLASS_NAME:
                return By.className(using);
            case CSS:
                return By.cssSelector(using);
            case ID:
            case UNSET:
                return By.id(using);
            case ID_OR_NAME:
                return new ByIdOrName(using);
            case LINK_TEXT:
                return By.linkText(using);
            case NAME:
                return By.name(using);
            case PARTIAL_LINK_TEXT:
                return By.partialLinkText(using);
            case TAG_NAME:
                return By.tagName(using);
            case XPATH:
                return By.xpath(using);

            default:
                // Note that this shouldn't happen (eg, the above matches all
                // possible values for the How enum)
                throw new IllegalArgumentException("Cannot determine how to locate element ");
        }
    }

    /**
     * Constructs a By object via ShortFindBy annotation
     * @param findBy CustomFindBy
     * @return By
     */
    private By buildByFromShortFindBy(CustomFindBy findBy) {
        if (!"".equals(findBy.className()))
            return By.className(findBy.className());

        if (!"".equals(findBy.css()))
            return By.cssSelector(findBy.css());

        if (!"".equals(findBy.id()))
            return By.id(findBy.id());

        if (!"".equals(findBy.linkText()))
            return By.linkText(findBy.linkText());

        if (!"".equals(findBy.name()))
            return By.name(findBy.name());

        if (!"".equals(findBy.partialLinkText()))
            return By.partialLinkText(findBy.partialLinkText());

        if (!"".equals(findBy.tagName()))
            return By.tagName(findBy.tagName());

        if (!"".equals(findBy.xpath()))
            return By.xpath(findBy.xpath());

        // Fall through
        return null;
    }

    /**
     * Validate CustomFindBy annotation
     * @param findBy CustomFindBy
     */
    private void assertValidFindBy(CustomFindBy findBy) {
        if (findBy.how() != null && findBy.using() == null) {
            throw new IllegalArgumentException("If you set the 'how' property, you must also set 'using'");
        }

        Set<String> finders = new HashSet<>();
        if (!"".equals(findBy.using()))
            finders.add("how: " + findBy.using());
        if (!"".equals(findBy.className()))
            finders.add("class name:" + findBy.className());
        if (!"".equals(findBy.css()))
            finders.add("css:" + findBy.css());
        if (!"".equals(findBy.id()))
            finders.add("id: " + findBy.id());
        if (!"".equals(findBy.linkText()))
            finders.add("link text: " + findBy.linkText());
        if (!"".equals(findBy.name()))
            finders.add("name: " + findBy.name());
        if (!"".equals(findBy.partialLinkText()))
            finders.add("partial link text: " + findBy.partialLinkText());
        if (!"".equals(findBy.tagName()))
            finders.add("tag name: " + findBy.tagName());
        if (!"".equals(findBy.xpath()))
            finders.add("xpath: " + findBy.xpath());

        // A zero count is okay: it means to look by name or id.
        if (finders.size() > 1) {
            throw new IllegalArgumentException(
                    String.format("You must specify at most one location strategy. Number found: %d (%s)",
                            finders.size(), finders.toString()));
        }
    }

}
