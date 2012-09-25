// All presidential candidates for 2012
start candidate=node:candidates('CAND_ID:*') where candidate.CAND_OFFICE='P' AND candidate.CAND_ELECTION_YR='2012' return candidate.CAND_NAME;

// Top 10 Presidential candidates according to number of campaign committees
start candidate=node:candidates('CAND_ID:*') match candidate<-[r:SUPPORTS]-(campaign) 
where candidate.CAND_OFFICE='P' AND candidate.CAND_ELECTION_YR='2012' 
return candidate.CAND_NAME, COUNT(campaign) as count
ORDER BY count desc LIMIT 10;

// find President Barack Obama
start obama=node:candidates('CAND_ID:*') WHERE obama.CAND_NAME =~ '.*OBAMA.*' return obama.CAND_NAME, obama.CAND_ID;

// lookup Obama by his candidate ID
start obama=node:candidates(CAND_ID='P80003338') return obama;

// find Presidential Candidate Mitt Romney
start romney=node:candidates('CAND_ID:*') WHERE romney.CAND_NAME =~ '.*ROMNEY.*' return romney.CAND_NAME, romney.CAND_ID;

// lookup Romney by his candidate ID
start romney=node:candidates(CAND_ID='P80003353') return romney;

// find the shortest path of funding between Obama and Romney
start romney=node:candidates(CAND_ID='P80003353'), obama=node:candidates(CAND_ID='P80003338') MATCH p=shortestPath(romney-[*..10]-obama) return p;

// 10 top individual contributions to Obama
start obama=node:candidates(CAND_ID='P80003338') match obama<-[:SUPPORTS]-(campaign)<-[:INDIVIDUAL_CONTRIBUTION]-(contribution) return contribution.NAME, contribution.TRANSACTION_AMT order by contribution.TRANSACTION_AMT desc limit 10;

// 10 top individual contributions to Romney
start romney=node:candidates(CAND_ID='P80003353') match romney<-[:SUPPORTS]-(campaign)<-[:INDIVIDUAL_CONTRIBUTION]-(contribution) return contribution.NAME, contribution.TRANSACTION_AMT order by contribution.TRANSACTION_AMT desc limit 10;

