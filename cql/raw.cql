// presidential campaign committees 2012
start candidate=node:candidates('CAND_ID:*') match (candidate)<-[:CAMPAIGNS_FOR]-(campaign) where candidate.CAND_OFFICE='P' AND candidate.CAND_ELECTION_YR='2012' return campaign.CMTE_NM, candidate.CAND_NAME;

start committee=node:committees('CMTE_ID:*') match p=(committee)<-[:CONTRIBUTION_FROM]-(recipient) return p limit 10;

// first 10 contributions made from committees to some recipient
start committee=node:committees('CMTE_ID:*') 
match (committee)<-[:CONTRIBUTION_FROM]-(contrib)-[:CONTRIBUTION_TO]->(recipient) 
return contrib.TRANSACTION_AMT, recipient.CMTE_NM limit 10;