package org.broadleafcommerce.gwt.server.changeset;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.configuration.AuditEntitiesConfiguration;
import org.hibernate.envers.entities.mapper.id.IdMapper;
import org.hibernate.envers.tools.query.QueryBuilder;
import org.hibernate.property.Getter;

public class ValidityAuditStrategy extends org.hibernate.envers.strategy.ValidityAuditStrategy {

	/** getter for the revision entity field annotated with @RevisionTimestamp */
    protected Getter revisionTimestampGetter = null;
    
	public void perform(Session session, String entityName, AuditConfiguration auditCfg, Serializable id, Object data, Object revision) {
		AuditEntitiesConfiguration audEntCfg = auditCfg.getAuditEntCfg();
		String auditedEntityName = audEntCfg.getAuditEntityName(entityName);

		// Update the end date of the previous row if this operation is expected
		// to have a previous row
		if (getRevisionType(auditCfg, data) != RevisionType.ADD) {
			/*
			 * Constructing a query: select e from audited_ent e where e.end_rev
			 * is null and e.id = :id
			 */

			QueryBuilder qb = new QueryBuilder(auditedEntityName, "e");

			// e.id = :id
			IdMapper idMapper = auditCfg.getEntCfg().get(entityName).getIdMapper();
			idMapper.addIdEqualsToQuery(qb.getRootParameters(), id, auditCfg.getAuditEntCfg().getOriginalIdPropName(), true);

			updateLastRevision(session, auditCfg, qb, id, auditedEntityName, revision);
		}

		// Save the audit data
		session.save(auditedEntityName, data);
	}

	@SuppressWarnings({"unchecked"})
    protected RevisionType getRevisionType(AuditConfiguration auditCfg, Object data) {
        return (RevisionType) ((Map<String, Object>) data).get(auditCfg.getAuditEntCfg().getRevisionTypePropName());
    }
	
	@SuppressWarnings({"unchecked"})
    protected void updateLastRevision(Session session, AuditConfiguration auditCfg, QueryBuilder qb,
                                    Object id, String auditedEntityName, Object revision) {
        String revisionEndFieldName = auditCfg.getAuditEntCfg().getRevisionEndFieldName();
        
        // e.end_rev is null
        qb.getRootParameters().addWhere(revisionEndFieldName, true, "is", "null", false);

        List<Object> l = qb.toQuery(session).list();

        // There should be one entry
        if (l.size() == 1) {
            // Setting the end revision to be the current rev
            Object previousData = l.get(0);
            ((Map<String, Object>) previousData).put(revisionEndFieldName, revision);

            if (auditCfg.getAuditEntCfg().isRevisionEndTimestampEnabled()) {
                // Determine the value of the revision property annotated with @RevisionTimestamp
            	Date revisionEndTimestamp;
            	String revEndTimestampFieldName = auditCfg.getAuditEntCfg().getRevisionEndTimestampFieldName();
            	Object revEndTimestampObj = this.revisionTimestampGetter.get(revision);

            	// convert to a java.util.Date
            	if (revEndTimestampObj instanceof Date) {
            		revisionEndTimestamp = (Date) revEndTimestampObj;
            	} else {
            		revisionEndTimestamp = new Date((Long) revEndTimestampObj);
            	}

            	// Setting the end revision timestamp
            	((Map<String, Object>) previousData).put(revEndTimestampFieldName, revisionEndTimestamp);
            }
            
            // Saving the previous version
            session.save(auditedEntityName, previousData);

        } else if (l.size() > 1) {
            throw new RuntimeException("Cannot find a single previous revision for entity " + auditedEntityName + " and id " + id);
        }
    }
}
