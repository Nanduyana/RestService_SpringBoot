/******************************************************************************* 

  Positions and Employees Delta Load - SQL Script #1
    2011-02-10  JMH Created for temporary manual delta load process
    2011-02-21  JMH Version 2 - switched to using user_ID for person/party UID's
                  removed the need to pull back UID's from base tables for
                  existing positions & employees.
                  Also made the selection criteria for positions match the 
                  selection criteria for employees. This will elimnate the cration
                  of positions with no employees.
    2011-02-23  JMH switched to using position name as position UID
    
    Executing this script is the 1st step of the process

    2014-05-21  KRISHNA SAGAR - Added new statement to update the old position name 
		to new position name, to address Blank Sales Rep production issue
********************************************************************************/
spool D:\RESULTS.txt

Set head Off
Set feed Off
set time on
set feedback on

BEGIN  -- the whole enchilada

---  Remove previous data from EIM tables
select * from tab;
delete from siebel.eim_position
where if_row_batch_num = 2222;

commit;

delete from siebel.eim_position
where if_row_batch_num = 3333;

commit;


delete from siebel.eim_employee
where if_row_batch_num = 2222;

commit;

delete from siebel.eim_contact
where if_row_batch_num = 3333;

commit;

delete from siebel.eim_contact1
where if_row_batch_num = 4444;

commit;

/* ---------------------------------------------------------------------------------------*/
/*                                                                                        */
/*                                                                                        */
/*          Luis Melero                         Date: 10/24/2012                          */
/*                                                                                        */
/*          Change for checking errors in employee load process                           */
/*          Process will mark records with errors for logging and not to be processed     */
/*          Process will also remove an employee position if the current                  */
/*          position will be changed with new one. In this case, after load               */
/*          process the employee will have only one position (the new one)                */
/*                                                                                        */
/*----------------------------------------------------------------------------------------*/

DECLARE
   Employee_Counter INTEGER;
   Position_Counter INTEGER;
   Current_position VARCHAR2(100);
BEGIN

DELETE FROM SIEBEL.EIM_EMPLOYEE WHERE IF_ROW_BATCH_NUM=5555;

COMMIT;

-- Check all the records to be processed for the right conditions

FOR rec IN (select  ROW_ID, 
        'Person' as party_type_cd, 
        EMP_END_DT, 
        decode(EMP_START_DT, null, '01-JAN-01', emp_start_dt) AS EMP_START_DT,
        con_status_cd as emp_stat_cd,
        con_email_addr,
        con_fst_name,
        con_job_title,
        con_last_name,
        con_mid_name,
        con_ph_num,
        con_status_cd,
        dept_code,
        substr(dept_name, 1, 30) as dept_name,
        job_code,
        upper(user_id) as PARTY_UID,
        replace(replace(pos_name, '(', '-'), ')', '-') as PP_PARTY_UID,  --ID of employee's position
	emp_id,
	rep_code,
        super_code,
        upper(user_id) as user_id,
        replace(replace(pos_name, '(', '-'), ')', '-') as pos_name,
        'FOR_IMPORT' as IF_ROW_STAT, 
        5555 as IF_ROW_BATCH_NUM,
	ACC_OU_ACCNT_LOC
  from siebel.cx_sales_rep y
  where user_id is not null
        and pos_name is not null
        and (EMP_ID is not null OR REP_CODE IS NOT NULL)
        and ACC_OU_ACCNT_LOC  IN ('UPDATE', 'NEW'))

LOOP
   -- Init var
   Employee_Counter:=0;
   Position_Counter:=0;
   -- Checking if the employee already exists in the DDBB
   
   select count(1) INTO Employee_Counter
   from SIEBEL.S_USER US, SIEBEL.S_PARTY EMPLOYEE, SIEBEL.S_PARTY_PER REL, SIEBEL.S_PARTY POS, SIEBEL.S_POSTN POSNAME
   where US.par_row_id=EMPLOYEE.row_id 
	AND EMPLOYEE.row_id=REL.person_id 
	AND REL.party_id=POS.row_id
	AND POS.row_id=POSNAME.par_row_id 	
	AND US.login=rec.PARTY_UID;   
   
   -- Check the position for other employees
   
   SELECT COUNT(1) INTO Position_Counter 
    from SIEBEL.S_USER US, SIEBEL.S_PARTY EMPLOYEE, SIEBEL.S_PARTY_PER REL, SIEBEL.S_PARTY POS, SIEBEL.S_POSTN POSNAME,SIEBEL.S_CONTACT EMPNAME
    where POSNAME.name= rec.pos_name
	AND US.par_row_id=EMPLOYEE.row_id 
	AND EMPLOYEE.row_id=REL.person_id 
	AND REL.party_id=POS.row_id
	AND POS.row_id=POSNAME.par_row_id 
	AND EMPLOYEE.row_id = EMPNAME.par_row_id
	AND US.login<>rec.PARTY_UID;
	
    IF Position_Counter >= 1 THEN
    -- The position exists for other user. Marked as error.
     UPDATE SIEBEL.CX_SALES_REP
     SET ACC_OU_ACCNT_LOC='ERROR_LOAD_001' || rec.ACC_OU_ACCNT_LOC
     WHERE ROW_ID= rec.ROW_ID;    
    ELSE   
	IF Employee_Counter>0 THEN
		-- Check if the employee has more than one position.
		
		Position_Counter:=0;
		
		SELECT count(1) INTO Position_Counter
		from SIEBEL.S_USER US, SIEBEL.S_PARTY EMPLOYEE, SIEBEL.S_PARTY_PER REL, SIEBEL.S_PARTY POS, SIEBEL.S_POSTN POSNAME
		where US.par_row_id=EMPLOYEE.row_id 
			AND EMPLOYEE.row_id=REL.person_id 
			AND REL.party_id=POS.row_id
			AND POS.row_id=POSNAME.par_row_id 	
			AND US.login=rec.PARTY_UID;
	
		IF Position_Counter> 1 THEN
		        -- The employee has currently more than one position. Marked as error
				
			UPDATE SIEBEL.CX_SALES_REP
			SET ACC_OU_ACCNT_LOC='ERROR_LOAD_002' || rec.ACC_OU_ACCNT_LOC
			WHERE ROW_ID= rec.ROW_ID;
	
		ELSE
			-- Employee is already in the system. The position must be changed to the new one.						   
			-- Get the Position for breaking the current relation.
			
			SELECT POS.PARTY_UID INTO Current_Position
			from SIEBEL.S_USER US, SIEBEL.S_PARTY EMPLOYEE, SIEBEL.S_PARTY_PER REL, SIEBEL.S_PARTY POS, SIEBEL.S_POSTN POSNAME
			where US.par_row_id=EMPLOYEE.row_id 
				AND EMPLOYEE.row_id=REL.person_id 
				AND REL.party_id=POS.row_id
				AND POS.row_id=POSNAME.par_row_id 	
				AND US.login=rec.PARTY_UID;				

			
			/* KRISHNA SAGAR 05/21/2014 - Below lines commentted as it deletes the employee association from Position record and 
			   creates new positon instead of updating the old position name to new position name.
			
			/*IF Current_Position <> rec.POS_NAME THEN
				-- Insert in EIM table for deleting
				INSERT INTO siebel.EIM_EMPLOYEE (ROW_ID,     
				PARTY_TYPE_CD,PARTY_UID,PP_PARTY_TYPE_CD,PP_PARTY_UID,IF_ROW_STAT,IF_ROW_BATCH_NUM)
				VALUES (rec.ROW_ID, rec.PARTY_TYPE_CD, rec.PARTY_UID,'Position', Current_position, rec.IF_ROW_STAT,  rec.IF_ROW_BATCH_NUM);		   
			END IF;*/
			

			-- KOMATIREDDY 11/21/2016 - Commenting out below code as this code is failing the delta process.
			-- KRISHNA SAGAR 05/21/2014 - Below condition is added to fix Blank sales rep prod issue, which is caused by above lines.
		/*	IF Current_Position <> rec.POS_NAME THEN			
				UPDATE SIEBEL.S_POSTN
				SET NAME = rec.POS_NAME
				where NAME = Current_position;

				commit;

				UPDATE SIEBEL.S_CONTACT 
				SET PR_HELD_POSTN_ID = (select row_id from SIEBEL.S_POSTN WHERE NAME = rec.POS_NAME)
				--WHERE ROW_ID = (SELECT ROW_ID FROM SIEBEL.S_USER WHERE LOGIN = rec.PARTY_UID);
				WHERE person_uid = rec.PARTY_UID;

				commit;

			END IF; */
--DHANASHRI sHINDE 03/08/2017 - Below condition is added to fix Blank sales rep prod issue, which is caused by above lines.
				
           		IF Current_Position <> rec.POS_NAME THEN	
					
				UPDATE SIEBEL.S_POSTN
				SET NAME = rec.POS_NAME
				where NAME = Current_position;
				commit;
				
 				UPDATE SIEBEL.S_PARTY
 				SET PARTY_UID = rec.POS_NAME
				WHERE PARTY_UID = Current_position;
				commit;
				
			END IF; 


			
		END IF;
	END IF;  
    END IF;

    COMMIT;
	
END LOOP;

END;


--- Prep EMP_ID's for subsequent processing, both positions and employees
/*
update siebel.cx_sales_rep
set emp_id = decode(emp_id, null, rep_code, cast(cast(emp_id as integer) as varchar(20))),
rep_code = DECODE(rep_code, null, cast(cast(emp_id as integer) as varchar(20)), rep_code) as varchar(20))
where pos_name is not null
      and (EMP_ID is not null OR REP_CODE IS NOT NULL)
      and ACC_OU_ACCNT_LOC  in ('UPDATE','NEW')
      and user_id is not null;

commit;
*/
--=================================================================
-- Positions
--=================================================================

---- Load Positions without parents

  BEGIN
-- use values from SELECT for FOR LOOP processing
FOR rec IN 
          (select ROW_ID,
                  'Position' as party_type_cd,
                  replace(replace(pos_name, '(', '-'), ')', '-') as PARTY_UID,
                  EMP_END_DT, 
                  decode(EMP_START_DT, null, '01-JAN-01', emp_start_dt) AS EMP_START_DT,
                  replace(replace(pos_name, '(', '-'), ')', '-') as POS_NAME,
                  upper(user_id) as PP_PARTY_UID, -- ID of employee assigned to this position
                  'FOR_IMPORT' as IF_ROW_STAT,
                  2222 as IF_ROW_BATCH_NUM
          from siebel.cx_sales_rep 
          where par_postn_name is null
                and pos_name is not null
                and (EMP_ID is not null OR REP_CODE IS NOT NULL)
                and ACC_OU_ACCNT_LOC  in ('UPDATE','NEW')
                and user_id is not null)
  LOOP
    insert into siebel.EIM_POSITION (ROW_ID,     PARTY_TYPE_CD,     PARTY_UID,     EMP_END_DT,     EMP_START_DT,     POS_NAME,     PP_PARTY_UID,     IF_ROW_STAT,     IF_ROW_BATCH_NUM)
      values (                       rec.ROW_ID, rec.PARTY_TYPE_CD, rec.PARTY_UID, rec.EMP_END_DT, rec.EMP_START_DT, rec.POS_NAME, rec.PP_PARTY_UID, rec.IF_ROW_STAT, rec.IF_ROW_BATCH_NUM);
  END LOOP;
END;

commit;

-- Re-Set PARTY_UID if position already exists
/*
update siebel.eim_position
set PARTY_UID = (select siebel.s_party.party_uid from siebel.s_postn, siebel.s_party
                    where siebel.eim_position.pos_name = siebel.s_postn.name
                    and siebel.s_postn.par_row_id = siebel.s_party.row_id
                    and siebel.s_party.party_uid is not null)
where if_row_batch_num = 2222
and POS_NAME IN (SELECT NAME FROM S_POSTN);

commit;
*/
-- Prep EIM_POSITION records for import



update siebel.eim_position
set pos_org_name = 'Default Organization',
pos_org_bu = 'Default Organization',
pos_org_loc = 'INTERNAL',
pos_org_bi = '0-R9NH',
PP_PARTY_TYPE_CD = 'Person',  -- Needs to be person to assign primary employee
--PP_PARTY_UID = party_uid,
ADMIN_ADJ_FLG = 'N',
ROOT_PARTY_FLG = 'N',
POSINICRETRORUNFLG = 'N',
POS_COMPENSATABLEF = 'N',
NAME = pos_name,
PP_REFERENCE_FLG = 'Y'
where IF_ROW_BATCH_NUM = 2222;

commit;

--=========================================
--- Load positions with parents
--=========================================


BEGIN
-- use values from SELECT for FOR LOOP processing
  FOR rec IN (select ROW_ID, 
                      'Position' as PARTY_TYPE_CD, 
                      replace(replace(pos_name, '(', '-'), ')', '-') as PARTY_UID,
                      upper(user_id) as PP_PARTY_UID, -- ID of employee assigned to this position
                      EMP_END_DT, 
                      decode(EMP_START_DT, null, '01-JAN-01', emp_start_dt) AS EMP_START_DT,
                      replace(replace(par_postn_name, '(', '-'), ')', '-') as PAR_POSTN_NAME, 
                      replace(replace(pos_name, '(', '-'), ')', '-') as POS_NAME, 
                      'FOR_IMPORT' as IF_ROW_STAT, 
                      3333 as IF_ROW_BATCH_NUM, 
                      replace(replace(par_postn_name, '(', '-'), ')', '-') as par_party_uid
                from siebel.cx_sales_rep y
                where par_postn_name is not null
                      and pos_name is not null
                      and rep_code is not null
                      and ACC_OU_ACCNT_LOC  IN ('UPDATE', 'NEW')
                      and user_id is not null
                      and (EMP_ID is not null OR REP_CODE IS NOT NULL))
  LOOP
    insert into siebel.EIM_POSITION (ROW_ID,     PARTY_TYPE_CD,     PARTY_UID,     EMP_END_DT,     EMP_START_DT,     PAR_POSTN_NAME,     POS_NAME,     IF_ROW_STAT,     IF_ROW_BATCH_NUM,     PAR_PARTY_UID,     PP_PARTY_UID)
      values (                       rec.ROW_ID, rec.PARTY_TYPE_CD, rec.PARTY_UID, rec.EMP_END_DT, rec.EMP_START_DT, rec.PAR_POSTN_NAME, rec.POS_NAME, rec.IF_ROW_STAT, rec.IF_ROW_BATCH_NUM, rec.PAR_PARTY_UID, rec.PP_PARTY_UID);
  END LOOP;
END;

commit;

-- Re-Set PARTY_UID if position already exists
/*
update siebel.eim_position
set PARTY_UID = (select siebel.s_party.party_uid from siebel.s_postn, siebel.s_party
                    where siebel.eim_position.pos_name = siebel.s_postn.name
                    and siebel.s_postn.par_row_id = siebel.s_party.row_id
                    and siebel.s_party.party_uid is not null)
where if_row_batch_num = 3333
and POS_NAME IN (SELECT NAME FROM siebel.S_POSTN);

commit;
*/

-- Re-Set PAR_PARTY_UID if position already exists
/*
update siebel.eim_position
set PAR_PARTY_UID = (select siebel.s_party.party_uid from siebel.s_postn, siebel.s_party
                    where siebel.eim_position.par_postn_name = siebel.s_postn.name
                    and siebel.s_postn.par_row_id = siebel.s_party.row_id
                    and siebel.s_party.party_uid is not null)
where if_row_batch_num = 3333
and PAR_POSTN_NAME IN (SELECT NAME FROM siebel.S_POSTN);

commit;
*/

-- Prep eim_position for import
update siebel.eim_position
set pos_org_name = 'Default Organization',
pos_org_bu = 'Default Organization',
pos_org_loc = 'INTERNAL',
pos_org_bi = '0-R9NH',
ADMIN_ADJ_FLG = 'N',
ROOT_PARTY_FLG = 'N',
POSINICRETRORUNFLG = 'N',
POS_COMPENSATABLEF = 'N',
NAME = pos_name,
PAR_PARTY_TYPE_CD = 'Position',
PAR_POSTN_BI = '0-R9NH',
PAR_POSTN_DIVN = 'Default Organization',
PAR_POSTN_BU = 'Default Organization',
PAR_POSTN_LOC = 'INTERNAL',
PP_PARTY_TYPE_CD = 'Person',
--PP_PARTY_UID = party_uid,
PP_REFERENCE_FLG = 'Y'
where IF_ROW_BATCH_NUM = 3333;

commit;


--=============================================================================
-- Employees
--=============================================================================

-- load EIM_EMPLOYEE
BEGIN
  FOR rec IN (select  ROW_ID, 
        'Person' as party_type_cd, 
        EMP_END_DT, 
        decode(EMP_START_DT, null, '01-JAN-01', emp_start_dt) AS EMP_START_DT,
        con_status_cd as emp_stat_cd,
        con_email_addr,
        con_fst_name,
        con_job_title,
        con_last_name,
        con_mid_name,
        con_ph_num,
        con_status_cd,
        dept_code,
        substr(dept_name, 1, 30) as dept_name,
        job_code,
        upper(user_id) as PARTY_UID,
        replace(replace(pos_name, '(', '-'), ')', '-') as PP_PARTY_UID,  --ID of employee's position
	emp_id,
	rep_code,
--        decode(emp_id, null, rep_code, cast(cast(emp_id as integer) as varchar(20))) as EMP_ID,
--        DECODE(rep_code, null, cast(cast(emp_id as integer) as varchar(20)), rep_code) as rep_code,
        super_code,
        upper(user_id) as user_id,
        replace(replace(pos_name, '(', '-'), ')', '-') as pos_name,
        'FOR_IMPORT' as IF_ROW_STAT, 
        2222 as IF_ROW_BATCH_NUM 
  from siebel.cx_sales_rep y
  where user_id is not null
        and pos_name is not null
        and (EMP_ID is not null OR REP_CODE IS NOT NULL)
        and ACC_OU_ACCNT_LOC  IN ('UPDATE', 'NEW'))
  LOOP
    insert into siebel.EIM_EMPLOYEE (ROW_ID,     PARTY_TYPE_CD,    PARTY_UID,     PP_END_DT,      HIRE_DT,          PP_START_DT,      CON_EMAIL_ADDR,     CON_FST_NAME,     CON_JOB_TITLE,    CON_LAST_NAME,      CON_MID_NAME,     CON_WORK_PH_NUM,  EMP_STAT_CD,      EMP_OFFICE_NUM, EMP_OFFICE_BLDG_CD, CON_EMP_NUM,  EMP_LIC_PLATE_NUM,  CON_NICK_NAME,  LOGIN,        PP_PARTY_UID,     IF_ROW_STAT,      IF_ROW_BATCH_NUM)
      values (                      rec.ROW_ID, rec.PARTY_TYPE_CD, rec.PARTY_UID, rec.EMP_END_DT, rec.EMP_START_DT, rec.EMP_START_DT, rec.CON_EMAIL_ADDR, rec.CON_FST_NAME, rec.CON_JOB_TITLE, rec.CON_LAST_NAME, rec.CON_MID_NAME, rec.CON_PH_NUM,   rec.EMP_STAT_CD,  rec.DEPT_CODE,  rec.DEPT_NAME,      rec.EMP_ID,   rec.JOB_CODE,       rec.SUPER_CODE, rec.USER_ID,  rec.pp_party_uid, rec.IF_ROW_STAT,  rec.IF_ROW_BATCH_NUM);
  END LOOP;
END;

commit;

-- Re-Set PARTY_UID if employee already exists EIM_EMPLOYEE
/*
update siebel.eim_employee
set PARTY_UID = (select siebel.s_contact.PERSON_UID from siebel.s_contact
                where siebel.s_contact.emp_num = siebel.eim_employee.con_emp_num)
where if_row_batch_num = 2222
and con_emp_num in (select emp_num from siebel.s_contact);

commit;
 */
-- Re-set PP_PARTY_UID if employee's position already exists: EIM_EMPLOYEE
/*
update siebel.eim_employee
set PP_PARTY_UID = (select siebel.s_party.PARTY_UID from siebel.s_party
                where siebel.s_party.name = siebel.eim_employee.CON_CON_ASST_NAME
                and rownum < 2)
where if_row_batch_num = 2222
and CON_CON_ASST_NAME in (select name from siebel.s_party);


commit;
*/
-- Prep EIM_EMPLOYEE for Import

update siebel.eim_employee
set name = con_last_name || ', ' || con_fst_name,
party_name = con_last_name || ', ' || con_fst_name,
emp_login_domain = 'YELLOWBOOK',
emp_login = login,
login_domain = 'YELLOWBOOK',
con_bi = '0-R9NH',
con_bu = 'Default Organization',
vis_bi = '0-R9NH',
vis_bu = 'Default Organization',
con_person_uid = party_uid,
resp_bi = '0-R9NH',
resp_bu = 'Default Organization',
--resp_name = 'YB_Prototype1',   -- Dev
resp_name = 'YB_Base_RO', -- Test
--resp_name = 'YB_All_Views_RO',  -- Production
PP_PARTY_TYPE_CD = 'Position',
CON_PR_HELD_POSTN = 'Y',
CON_ACTIVE_FLG = 'Y',
CON_DISACLEANSEFLG = 'Y',
CON_DISPIMGAUTHFLG = 'Y',
CON_EMAILSRUPD_FLG = 'Y',
CON_EMP_FLG = 'Y',
CON_PO_PAY_FLG = 'N',
CON_PRIV_FLG = 'N',
CON_PROSPECT_FLG = 'N',
CON_PTSHPCONTACTFL = 'N',
CON_PTSHPKEYCONFLG = 'N',
CON_SENDSURVEY_FLG = 'N',
CON_SUPPRESSEMAILF = 'N',
CON_SUPPRESSFAXFLG = 'N',
ACCEPT_SR_ASGN_FLG = 'Y',
BONUS_ELIGIBLE_FLG = 'N',
CNTRCTR_FLG = 'N',
EMP_CPFINALAPPRFLG = 'N',
INT_NEWS_APPR_FLG = 'N',
LOY_TIER_APPR_FLG = 'N',
MERIT_ELIGIBLE_FLG = 'N',
PROMO_ELIGIBLE_FLG = 'N',
STOCK_ELIGIBLE_FLG = 'N',
STORE_BUDGET_FLG = 'N',
STORE_FORECAST_FLG = 'N',
ADMIN_ADJ_FLG = 'N',
ROOT_PARTY_FLG = 'N',
USER_FLG = 'Y',
PP_REFERENCE_FLG = 'Y',
--CON_CON_ASST_NAME = null,
if_row_stat = 'FOR_IMPORT'
where if_row_batch_num = 2222;

COMMIT;

------ Load EIM_CONTACT ----------------------------

BEGIN
  FOR rec IN (select  row_id, 
--                      decode(emp_id, null, rep_code, cast(cast(emp_id as integer) as varchar(20))) as EMP_ID,
			emp_id,
                      upper(user_id) as PARTY_UID,
                      con_last_name || ', ' || con_fst_name as name,
                      3333 as IF_ROW_BATCH_NUM 
              from siebel.cx_sales_rep y
              where user_id is not null
                    and pos_name is not null
                    and (EMP_ID is not null OR REP_CODE IS NOT NULL)
                    and ACC_OU_ACCNT_LOC  IN ('UPDATE', 'NEW')
)
  LOOP
    insert into siebel.EIM_CONTACT (ROW_ID,     CON_PERSON_UID, CON_PRIV_FLG, ADMIN_ADJ_FLG, NAME,     PARTY_TYPE_CD, PARTY_UID,     ROOT_PARTY_FLG, IF_ROW_STAT, IF_ROW_BATCH_NUM)
      values (                      rec.ROW_ID, rec.party_UID,     'N',          'N',        rec.name, 'Person',      rec.PARTY_UID, 'N',            'FOR_IMPORT', rec.IF_ROW_BATCH_NUM);
  END LOOP;
END;
 
commit;

-- Re-Set PARTY_UID if employee already exists  EIM_CONACT ------
/*
update siebel.eim_contact
set PARTY_UID = (select siebel.s_contact.PERSON_UID from siebel.s_contact
                where siebel.s_contact.emp_num = siebel.eim_contact.LICENSE_NUM)
where if_row_batch_num = 3333
and LICENSE_NUM in (select emp_num from siebel.s_contact);

commit;
*/

-- Prep eim_contact for import -----

UPDATE siebel.EIM_CONTACT
SET CON_BU = 'Default Organization',
con_bi = '0-R9NH',
CB_BI  = '0-R9NH',
CB_BU = 'Default Organization',
IF_ROW_STAT = 'FOR_IMPORT'
--LICENSE_NUM = NULL,
--con_person_uid = party_uid
where if_row_batch_num = 3333;

commit;

------ Load EIM_CONTACT1 ----------------------------

BEGIN
  FOR rec IN (select  ROW_ID, 
                      upper(user_id) as PERSON_UID,
			emp_id,
--                      decode(emp_id, null, rep_code, cast(cast(emp_id as integer) as varchar(20))) as EMP_ID,
                      replace(replace(pos_name, '(', '-'), ')', '-') as PC_POSTN_NAME,
                      4444 as IF_ROW_BATCH_NUM 
              from siebel.cx_sales_rep y
              where user_id is not null
                    and pos_name is not null
                    and (EMP_ID is not null OR REP_CODE IS NOT NULL)
                    and ACC_OU_ACCNT_LOC  IN ('UPDATE', 'NEW')
)
  LOOP
    insert into siebel.EIM_CONTACT1 (ROW_ID,     CON_PERSON_UID, CON_PRIV_FLG, PARTY_TYPE_CD, PARTY_UID,      PC_ACTIVE_FLG, PC_POSTN_NAME,     IF_ROW_STAT,  IF_ROW_BATCH_NUM)
      values (                       rec.ROW_ID, rec.PERSON_UID, 'N',         'Person',       rec.PERSON_UID, 'Y',           rec.PC_POSTN_NAME, 'FOR_IMPORT', rec.IF_ROW_BATCH_NUM);
  END LOOP;
END;
 
commit;

-- Re-Set PARTY_UID if employee already exists EIM_CONTACT1 ------------------
/*
update siebel.eim_contact1
set PARTY_UID = (select siebel.s_contact.PERSON_UID from siebel.s_contact
                where siebel.s_contact.emp_num = siebel.eim_contact1.BRICK_NAME)
where if_row_batch_num = 4444
and BRICK_NAME in (select emp_num from siebel.s_contact);

commit;
*/
-- Prep eim_contact1 for import ----------
Nandu
UPDATE siebel.EIM_CONTACT1
SET PC_POSTN_BI = '0-R9NH',
pc_postn_bu = 'Default Organization',
PC_POSTN_DIVN = 'Default Organization',
PC_POSTN_LOC = 'INTERNAL',
PC_ROW_STATUS = 'Y',
--BRICK_NAME = NULL,
if_row_stat = 'FOR_IMPORT'
--con_person_uid = party_uid
where if_row_batch_num = 4444;

commit;

END; -- whole enchilada
/
spool off;
exit;