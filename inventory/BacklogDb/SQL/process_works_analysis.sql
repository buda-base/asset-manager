-- calculate aging
select pw.process_work, project.process_project, pws.process_create_date, pws.obs_date,  datediff(pws.obs_date , pws.process_create_date)  as aging from process_work_states pws
join process_projects project on pws.process_project_id = project.idprocess_project
join process_works pw on pws.process_work_id = pw.idprocess_work;

/* get the total count of works by obs date
-- subquery: get the works still pending. ie in a project (or a host)
-- determine date of transition for a work.

*/
-- get the count of works in each project
-- get the age  for each work in projects
-- get the transaction history for each work:
--    -
select first_obs_date,count(*) from process_work_states

group by  first_obs_date order by first_obs_date desc;

select distinct obs_date from process_work_states;

select max(obs_date) from process_work_states;
-- get works which are transitioned out
-- problem is if one project has a missing obs_date, then the whole thing is skewed
select  pw.process_work, pp.process_project  from process_work_states pws
join process_works pw on pws.process_work_id = pw.idprocess_work
join process_projects pp on pws.process_project_id = pp.idprocess_project
where pws.last_obs_date < (select max(last_obs_date) from process_work_states);


-- get the last date each project was seen
select  pp.process_project, pws.last_obs_date, count(process_work_id) from process_work_states pws
join process_projects pp on pws.process_project_id = pp.idprocess_project
group by pp.process_project
order by pws.last_obs_date desc;

select  distinct pp.process_project, pws.last_obs_date,  count(process_work_id) from process_work_states pws
join process_projects pp on pws.process_project_id = pp.idprocess_project
group by pws.last_obs_date
order by pws.last_obs_date desc;

select  process_project  from process_work_states pws
  join process_projects pp on pws.process_project_id = pp.idprocess_project
where pws.last_obs_date = (select max(last_obs_date) from process_work_states);


select  pw.process_work, pp.process_project  from process_work_states pws
join process_works pw on pws.process_work_id = pw.idprocess_work
join process_projects pp on pws.process_project_id = pp.idprocess_project
order by pws.create_time desc limit 10;

