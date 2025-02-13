create table bus (
    BusNumber varchar(7),
	color varchar(20),
    Model varchar(25),
    Capacity int,
    constraint pk_bus primary key (BusNumber)
)

create table driver (
    DriverId int,
	BusNumber varchar(7),
    Name varchar(100),
    License_number varchar(50),
    Contact varchar(100),
    constraint pk_driver primary key (DriverId),
	constraint fk_busNumber foreign key (BusNumber) references Bus(BusNumber)
)

create table route (
    RouteId int identity (1,1),
    Source varchar(100),
    Destination varchar(100),
    Distance float,
    Duration int,
    constraint pk_route primary key (routeID)
)

create table schedule (
    ScheduleId int identity (1,1),
    RouteId int,
    BusId varchar(7),
    Departure_time datetime,
    Arrival_time datetime,
    Fare decimal(10,2),
    constraint pk_schedule primary key (scheduleid),
    constraint fk_schedule_route foreign key (routeid) references route(routeid),
    constraint fk_schedule_bus foreign key (busid) references bus(busNumber)
)

create table passenger (
    passengerId varchar(20),
    Name varchar(50),
    ContactNumber varchar(12),
	Email varchar(30),
	Gender varchar(10),
    Address varchar(100),
    constraint pk_passenger primary key (passengerid)
)

create table reservation (
    ReservationId int identity(1,1),
    PassengerId varchar(20),
    ScheduleId int,
    Seat_number varchar(10),
    status varchar(20),
    constraint pk_reservation primary key (reservationid),
    constraint fk_reservation_passenger foreign key (passengerid) references passenger(passengerid),
    constraint fk_reservation_schedule foreign key (scheduleid) references schedule(scheduleid)
)

create table payment (
    paymentId int identity(1,1),
    ReservationId int,
    Amount decimal(10,2),
    Payment_method varchar(50),
    Transaction_date datetime,
    constraint pk_payment primary key (paymentid),
    constraint fk_payment_reservation foreign key (reservationid) references reservation(reservationid)
)

create table employee (
    Employee_id int identity (1,1),
    Name varchar(30),
    Position varchar(15),
    Contact_info varchar(11),
    constraint pk_employee primary key (employee_id)
)

create table Ticket (
    TicketId int identity (1,1),
    ReservationId int,
    Issue_date datetime,
    Expiry_date datetime,
    constraint pk_ticket primary key (ticketid),
    constraint fk_ticket_reservation foreign key (reservationid) references reservation(reservationid)
)

create table feedback (
    feedback_id int identity (1,1),
    passengerId varchar(20),
    ScheduleId int,
    rating int,
    Comments varchar(255),
    Feedback_date datetime,
    constraint pk_feedback primary key (feedback_id),
    constraint fk_feedback_passenger foreign key (passengerid) references passenger(passengerid),
    constraint fk_feedback_schedule foreign key (scheduleid) references schedule(scheduleid)
)

create table Admin (
    username varchar(25),
    password varchar(25),
)

CREATE TABLE ContactUs (
    firstname VARCHAR(30) NOT NULL,
    lastname VARCHAR(30) NOT NULL,
    email VARCHAR(255) NOT NULL,
    comments TEXT,
    CONSTRAINT PK_ContactUs PRIMARY KEY (email)
);



--ADDING CONSTRAINTS
alter table schedule 
add constraint fk_scheduleroute foreign key (routeid) references route(routeid);

-->   DRIVER UNIQUE CONSTRAINTS
alter table driver 
add constraint UC_contact Unique (Contact)

alter table driver
add constraint UC_Lic Unique (License_number)

-->>  PASSENGER TABLE
alter table passenger
add Password varchar(35)

alter table passenger
alter column ContactNumber varchar(12)

alter table passenger
drop column Address

alter table passenger
add constraint UC_Email Unique(Email)AA

alter table passenger
add constraint UC_ContactPassenger Unique(ContactNumber)

alter table passenger
alter column Gender varchar(30)

-- >> EMPLOYEE TABLE
alter table employee
alter column Position varchar(30)

alter table employee
alter column contact_info varchar(12)

alter table employee
add constraint Uc_ContactEmployee Unique (Contact_info)

ALTER TABLE Employee ALTER COLUMN Position VARCHAR(30);
ALTER TABLE Employee ALTER COLUMN Contact_info VARCHAR(12);
-- >> ADMIN TABLE
alter table admin
add constraint Uc_nameAdmin unique (userName)

alter table admin 
add Rank varchar(20)

EXEC sp_rename 'admin.Rank',  'AdminLevel', 'COLUMN';


--->> LOG TABLE
create table LogTable (
    LogID int identity(1,1),
    AdminID varchar(25) not null,
    AdminName varchar(25) not null,
    EventOn varchar(50) not null,
    Action varchar(10) not null,
    AffectedID varchar(10) not null,
    LogTimestamp datetime DEFAULT getdate()

	constraint pk_logId primary key (LogID)
)

-->>>CONTACT US
ALTER TABLE ContactUs
ADD CONSTRAINT UQ_Email UNIQUE (email);

-- Add constraints to ensure the length of firstname and lastname are 30 characters
ALTER TABLE ContactUs
ADD CONSTRAINT CHK_Firstname_Length CHECK (LEN(firstname) <= 30)
ALTER TABLE ContactUs
ADD CONSTRAINT CHK_Lastname_Length CHECK (LEN(lastname) <= 30)
-----------------------------------------------------------------------------
--insert values
-- 1 -- >BUS TABLE
INSERT INTO Bus (BusNumber, Color, Model, Capacity)
VALUES ('BUS-123', 'Red', 'Volvo B9R', 55),
       ('BUS-234', 'Blue', 'Scania Irizar i6', 45),
       ('BUS-345', 'Green', 'Mercedes-Benz Tourismo', 60),
       ('BUS-456', 'Yellow', 'MAN Lions Coach', 39),
       ('BUS-567', 'White', 'Yutong ZK6127H', 53);



--2  --> Driver table
INSERT INTO Driver (DriverId, BusNumber, Name, License_number, Contact)
VALUES (1001, 'BUS-123', 'John Smith', 'DL12345678', '03451234567'),
       (1002, 'BUS-234', 'Jane Doe', 'DL87654321', '03009876543'),
       (1003, 'BUS-345', 'Michael Brown', 'DL23456178', '03213456789'),
       (1004, 'BUS-456', 'Alice Young', 'DL56789012', '03112345678'),
       (1005, 'BUS-567', 'David Miller', 'DL98765432', '03334567890');
	   

--3 --> Route table
INSERT INTO Route (Source, Destination, Distance, Duration)
VALUES ('Islamabad', 'Lahore', 340, 6),
       ('Karachi', 'Peshawar', 1300, 20),
       ('Multan', 'Faisalabad', 350, 7),
       ('Quetta', 'Sukkur', 420, 8),
       ('Rawalpindi', 'Gujranwala', 200, 4);
	   
	   

--4 --> Schedule Table
INSERT INTO Schedule (RouteId, BusId, Departure_time, Arrival_time, Fare)
VALUES (1, 'BUS-123', '2024-05-12 07:00:00', '2024-05-12 13:00:00', 2500),
       (2, 'BUS-234', '2024-05-13 10:00:00', '2024-05-14 06:00:00', 4000),
       (3, 'BUS-345', '2024-05-14 15:00:00', '2024-05-15 00:00:00', 3200),
       (4, 'BUS-456', '2024-05-15 08:00:00', '2024-05-15 18:00:00', 2800),
       (5, 'BUS-567', '2024-05-16 12:00:00', '2024-05-16 16:00:00', 1800);


-- 5 --> Passenger Table
INSERT INTO Passenger (PassengerId, Name, ContactNumber, Email, Gender)
VALUES ('P-1001', 'Ali Khan', '03478901234', 'ali.khan@email.com', 'Male'),
       ('P-1002', 'Fatima Ahmed', '03332145678', 'fatima.ahmed@email.com', 'Female'),
	   ('P-1003', 'Sana Ali', '0300-4567890', 'sana@example.com', 'Female'),
		('P-1004', 'Zain Ahmed', '0334-1234567', 'zain@example.com', 'Male'),
		('P-1005', 'Fatima Bashir', '0312-3456789', 'fatima@example.com', 'Female');
       
	  

-- 6 -->> Reservation table
INSERT INTO Reservation (PassengerId, ScheduleId, Seat_number, Status)
VALUES ('P-1001', 9, 12,'Confirmed'),
       ('P-1002', 10, 15,'Confirmed'),
       ('P-1003', 11, 19, 'Pending'),
       ('P-1004', 12, 7, 'Confirmed'),
       ('P-1005', 8, 22,'Pending');


-- 7 -->> Payment table
INSERT INTO Payment ( ReservationId, Amount, Payment_method, Transaction_date) VALUES
(4, 3050.00, 'Cash on Delivery', '2024-05-09 10:00:00'),
(5, 5200.00, 'Credit Card', '2024-05-19 15:00:00'),
( 7, 2850.00, 'Debit Card', '2024-05-29 12:00:00');

--8 --> EMPLOYEE table
INSERT INTO Employee ( Name, Position, Contact_info)
VALUES ('Umaima Khan', 'Manager', '02134567890'),
       ('Babar Ali', 'Ticketing Staff', '03098765432'),
       ('Sadia Fatima', 'Customer Service', '03112345678'),
       ('Zeeshan Ahmed', 'Accounts', '03245678901'),
       ( 'Maria Joseph', 'Operations', '03378901234');
	  
-- 9 -->Ticket Table
INSERT INTO Ticket (ReservationId, Issue_date, Expiry_date)
VALUES (4, '2024-05-11 10:00:00', '2024-05-12 18:00:00'),  -- Assuming reservation 101 exists for passenger booking this ticket
       (5, '2024-05-11 11:00:00', '2024-05-14 12:00:00'),  -- Assuming reservation 102 exists for passenger booking this ticket
       (6, '2024-05-11 15:00:00', '2024-05-15 06:00:00'),  -- Assuming reservation 103 exists for passenger booking this ticket
       (7, '2024-05-11 17:00:00', '2024-05-16 10:00:00')  -- Assuming reservation 104 exists for passenger booking this ticket

-->Feedback Table
INSERT INTO Feedback ( passengerId, ScheduleId, rating, Comments) VALUES
( 'P-1001', 8, 4, 'The bus was clean and comfortable, but the departure was delayed by an hour.'),
( 'P-1002', 9, 5, 'Excellent service! The staff was very helpful and the journey was smooth.'),
('P-1003', 10, 2, 'The air conditioning was not working properly, making the journey quite uncomfortable.'),
( 'P-1004', 11, 3, 'The bus arrived on time, but the seats were not very spacious.'),
('P-1005', 12, 5, 'A fantastic travel experience! Would definitely recommend this service.')

-->
Update Admin
set rank='Super'
where username= 'Asghar'

------------------------------------------      PROCEDURES FOR USER SIDE PANEL ------------------------------


-->DISPLAY SCHEDULE FOR THE SEARCHED SOURCE AND DESTINATION
CREATE PROCEDURE GetScheduleBySourceAndDestination
    @Source varchar(100),
    @Destination varchar(100)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT s.ScheduleId, s.RouteId,s.BusId, r.Source, r.Destination, s.Departure_time,s.Arrival_time,s.Fare
    FROM schedule s
    INNER JOIN route r ON s.RouteId = r.RouteId
    WHERE r.Source = @Source AND r.Destination = @Destination;
END

--> DISPLAY ALL DISTINCT SOURCES
Create procedure DisplayAllSources
as
begin
select distinct source from route
end
-->DISPLAY ALL DISTINCT DESTINATION
Create procedure DisplayAllDestination
as
begin
select distinct destination from route
end
-->
-->display info for ticket
Create procedure DisplayForTicket
	@passengerid varchar(20)
as
begin
select p.Name,b.BusNumber,b.Model,b.color,rou.Source,rou.Destination,s.Departure_time,s.Fare,r.Seat_number
from reservation r
inner join schedule s on s.ScheduleId=r.ScheduleId
inner join bus b on b.BusNumber=s.BusId
inner join route rou on rou.RouteId=s.RouteId
inner join passenger p on p.passengerId=r.PassengerId
where r.PassengerId= @passengerid
end

-->display for payment
Create procedure DisplayInfoForPayment
	@passengerid varchar(20)
as
begin
select ReservationId,rou.Source,rou.Destination,r.Seat_number,s.Fare
from reservation r
inner join schedule s on s.ScheduleId= r.ScheduleId
inner join route rou on rou.RouteId =s.RouteId
where r.PassengerId= @passengerid
end

----
--> procedure to display info for feedback
CREATE PROCEDURE DisplayInfoForFeedback
    @passengerid VARCHAR(20),
    @scheduleid INT
AS
BEGIN
    SELECT TOP 1
        p.Name,
        p.Email,
        rou.Source,
        rou.Destination,
        r.Seat_number,
        s.Fare
    FROM 
        reservation r
        INNER JOIN passenger p ON p.passengerId = r.PassengerId
        INNER JOIN schedule s ON s.ScheduleId = r.ScheduleId
        INNER JOIN route rou ON rou.RouteId = s.RouteId
        LEFT JOIN feedback f ON f.ScheduleId = s.ScheduleId
    WHERE  
        r.status = 'Confirmed' 
        AND r.PassengerId = @passengerid 
        AND (f.ScheduleId IS NULL OR f.ScheduleId <> @scheduleid)
    ORDER BY
        r.ScheduleId DESC; -- Order by ScheduleId descending to get the latest one
END

--->> get reservaion id



select *from feedback

-->> INSERT INTO PAYMENT TABLE 
select *from payment
Create procedure InsertIntoPayment
	@reservationid int, @amount decimal, @paymentmethod varchar(30),@transDate varchar(50)
as
begin
insert into payment(ReservationId,Amount,Payment_method,Transaction_date)
values (@reservationid,@amount,@paymentmethod,@transDate)
end


-->> UPDATE STATUS AFTER BILLING
create procedure UpdateStatusAfterBilling
	@passengerid varchar(20)
as
begin
Update reservation
set status='Confirmed'
where PassengerId= @passengerid
end

--->> FIND SCHEDULE ID
create procedure FindScheduleID
	@passengerid varchar(20)
as
begin
select ScheduleId
from reservation
where PassengerId=@passengerid

end
select *from passenger
select *from schedule
select *from reservation
select *from payment
select *from ticket
select *from feedback


-->> INSERT INTO FEEDBACK TABLE
create procedure InserIntoFeedback
	@passengerid varchar(20), 
	@ScheduleID int,
	@rating int,
	@comments varchar(255)
as
begin
Insert into Feedback 
values (@passengerid, @ScheduleID,@rating,@comments)
end



-->> Display the reservation status of passenger

create procedure DisplayStatus
	@passengerid varchar(20)
as
begin
select top 1 status from reservation
where PassengerId=@passengerid
order by status desc
end


exec DisplayStatus 'asghar01'


--->> ALTERING PROCEDURE FOR DISPLAY PAYMENT
Alter procedure DisplayInfoForPayment
	@passengerid varchar(20)
as
begin
select ReservationId,rou.Source,rou.Destination,r.Seat_number,s.Fare
from reservation r
inner join schedule s on s.ScheduleId= r.ScheduleId
inner join route rou on rou.RouteId =s.RouteId
where  r.status='Pending' AND r.PassengerId= @passengerid
end

--> ALTERING PROCEDURE FOR DISPLAY FOR TICKET
Alter procedure DisplayForTicket
	@passengerid varchar(20)
as
begin
select p.Name,b.BusNumber,b.Model,b.color,rou.Source,rou.Destination,s.Departure_time,s.Fare,r.Seat_number
from reservation r
inner join schedule s on s.ScheduleId=r.ScheduleId
inner join bus b on b.BusNumber=s.BusId
inner join route rou on rou.RouteId=s.RouteId
inner join passenger p on p.passengerId=r.PassengerId
where r.status='Confirmed' and r.PassengerId= @passengerid
end

-----------
--> Procedure to display number of past reservations
Create Procedure CountNumberOfReservations
	@passengerid varchar(20)
as
begin
select count(*) as 'Total Reservations'
from reservation
where PassengerId= @passengerid
end

select *from reservation
select *from ticket

-->PROCEDURE TO PRINT EVERY DETAILS OF PASSENGER
create procedure DisplayDetailsOfPassenger
	@id varchar(20)
as
begin
select Name,ContactNumber,Email,Password
from passenger
where passengerId=@id
end
select *from passenger

----> UPDATE VALUE OF PASSENGER
Create procedure UpdateValueOfPassenger
	@id varchar(20),@name varchar(50),@contact varchar(12),@email varchar(30),@password varchar(35)
as
begin
Update passenger
set Name=@name,ContactNumber=@contact,Email=@email,Password=@password
where passengerId=@id;
end

------>> Insert Into TicketTable
-->Get reservation id from passenger id
create procedure GetReservationID
	@passid varchar(20)
as
begin
select ReservationId from reservation
where PassengerId= @passid
end
-->Insert
Create Procedure InsertIntoTicket
	@reservationID int, @issueDate DateTime , @ExpiriryDate datetime
as
begin
Insert into Ticket
Values(@reservationID,@issueDate,@ExpiriryDate)
end


-->>A reservations
Create Procedure DisplayReservations
	@passid varchar(20)
as
begin
select ro.Source,ro.Destination,s.Departure_time,r.Seat_number,s.Fare
from reservation r
inner join schedule s on s.ScheduleId=r.ScheduleId
inner join route ro on ro.RouteId=s.RouteId
where r.PassengerId = @passid;
end

select *from passenger
select *from reservation
select *from schedule
select *from payment
select *from Ticket


-->Get Departure Time from Reservationid
create procedure GetDepartureTime
	@resid int
as
begin
Select Departure_time from schedule s
inner join reservation r on r.ScheduleId= s.ScheduleId
where r.ReservationId=@resid
end



------------------------------------------      PROCEDURES FOR ADMIN SIDE PANEL ------------------------------

-->> Check admin at login page
Create Procedure CheckAdmins
	@adminName varchar(20), @pass varchar(30)
as
begin
Select username, password from admin
where username = @adminName AND password = @pass
end


--->> ADMIN USER Page 
Create Procedure DisplayAdminHomepage
	@username varchar(30)
as
begin
select count(distinct b.busnumber) as numberofbuses,
    count(distinct e.employee_id) as numberofemployees,
    count(distinct d.driverid) as numberofdrivers,
    count(distinct p.passengerid) as numberofpassengers
from admin a
left join bus b on 1=1
left join driver d on b.busnumber = d.busnumber
left join passenger p on 1=1
left join employee e on 1=1
where a.username = @username
group by a.username;
end


--->> ADMIN ADD BUS
create procedure AddBus
	@busno varchar(7), @color varchar(20),@model varchar(25), @cap int
as begin
Insert into bus
values(@busno,@color,@model,@cap)
end

select *from bus

-->> Admin Display All BUS
create view vw_DisplayBus as
select *
from bus

-->CREATE PROCEDURE
Create procedure DisplayBus 
as
begin
select *from vw_DisplayBus
end

-->>ADMIN DELETE BUS BY BUSNUMBER
create procedure DelteBus
	@busNumber varchar(7)
as
begin
Delete from Bus
where BusNumber=@busNumber
end

-->> AMDIN UPDATE BUS DETAILS
Create procedure UpdateBus
	@id varchar(7),@color varchar(30),@model varchar(30), @cap int
as
begin
Update bus
set color=@color,Model=@model,Capacity=@cap
where BusNumber=@id
end

------->> Admin add driver
-- Display unassigned bus
CREATE PROCEDURE DisplayUnassignedBuses
AS
BEGIN
    SELECT b.BusNumber
    FROM bus b
    LEFT JOIN driver d ON b.BusNumber = d.BusNumber
    WHERE d.DriverId IS NULL;
END;

--insert values of driver
create procedure insertIntoDriver
    @DriverId int,@BusNumber varchar(7),@Name varchar(100),@License_number varchar(50),@Contact varchar(100)
as
begin
    insert into driver
    values(@DriverId, @BusNumber, @Name, @License_number, @Contact)
end

--Update Driver
create procedure updateDriver
	@driverid int, @newbusnumber varchar(7), @newname varchar(100), @newlicensenumber varchar(50), @newcontact varchar(100)
as
begin
update driver 
set BusNumber = @newbusnumber, Name = @newname, License_number = @newlicensenumber, Contact = @newcontact 
where DriverId = @driverid;
end;

--Delete Driver
create procedure DeleteDriverById 
	@DriverId int
as
begin
Delete from driver
where DriverId = @DriverId;
end;

--Display Driver
create view vw_DisplayDrivers as
select *
from driver

create procedure DisplayDrivers
as
begin
Select *from vw_DisplayDrivers
end


--ROUTE PAGE ADMIN
----> insert into route
create procedure insertRoute 
	@source varchar(100), @destination varchar(100), @distance float, @duration int
as
begin
Insert into Route (Source, Destination, Distance, Duration)
values (@source, @destination, @distance, @duration);
end

---->DELETE from route
create procedure deleteRoute 
	@routeid int
as
begin
Delete from route where RouteId = @routeid;
end;


---->UPDATE in route
create procedure updateRoute
@routeid int, @source varchar(100), @destination varchar(100), @distance float, @duration int
as
begin
Update route 
set Source = @source, Destination = @destination, Distance = @distance, 
	Duration = @duration where RouteId = @routeid;
end

---->>Display route
Create view vw_DisplayRoute
as Select * from Route

create procedure DisplayRoute
as begin
select *from vw_DisplayRoute
end

--check if source and destination already Exist
Create procedure SourceDestinationAlreadyExist
	@source varchar(100), @destination varchar(100)
as 
begin
select COUNT(*) as countNo 
from route
WHERE Source = @source and Destination = @destination
end

------->>ADMIN ADD EMPLOYEE PANEL
--> insert Employee
create procedure insertEmployee 
	@name varchar(30), @position varchar(15), @contact_info varchar(11)
as
begin
insert into employee (Name, Position, Contact_info)
values (@name, @position, @contact_info);
end;

-->Update Employee
create procedure updateEmployee 
	@employee_id int, @name varchar(30), @position varchar(15), @contact_info varchar(11)
as
begin
Update employee set Name = @name, Position = @position, Contact_info = @contact_info where Employee_id = @employee_id;
end;

-->Delete Employee
create procedure deleteEmployee
	@employee_id int
as
begin
    delete from employee where Employee_id = @employee_id;
end;

-->Display Employee
CREATE VIEW vw_DisplayEmployee AS
select *
from employee

create procedure DisplayEmploye
as
begin
Select *from vw_DisplayEmployee
end

------------>>>> SCHEDULE ADMIN PANEL
-- DISPLAY SCHEDULE
Create view vw_DisplaySchedule
AS SELECT * FROM schedule

create procedure DisplaySchedule
as begin Select *from vw_DisplaySchedule end 
--INSERT SCHEDULE
create procedure InsertSchedule
    @RouteId INT,@BusId VARCHAR(7),@DepartureTime DATETIME,@ArrivalTime DATETIME,@Fare DECIMAL(10, 2)
AS
begin
insert into schedule (RouteId, BusId, Departure_time, Arrival_time, Fare)
values (@RouteId, @BusId, @DepartureTime, @ArrivalTime, @Fare);
end;

--UPDATE SCHEDULE
create procedure UpdateSchedule
    @ScheduleId INT,@RouteId INT,@BusId VARCHAR(7),@DepartureTime DATETIME,@ArrivalTime DATETIME, @Fare DECIMAL(10, 2)
as
begin
update schedule
set RouteId = @RouteId,BusId = @BusId,Departure_time = @DepartureTime,Arrival_time = @ArrivalTime,Fare = @Fare
where ScheduleId = @ScheduleId;
end

--DELETE SCHEDULE
CREATE PROCEDURE DeleteSchedule
    @ScheduleId INT
AS
BEGIN
    DELETE FROM schedule
    WHERE ScheduleId = @ScheduleId;
END;

--->> GETING ALL ROUTES ID
CREATE PROCEDURE GetAllRouteIDs
AS
BEGIN
    SELECT RouteId FROM route;
END;

---->>GETTING BUS ID THAT HAS NOT BEEN REGISTERED IN SCHEDULE
CREATE PROCEDURE GetAvailableBusIDs
AS
BEGIN
    SELECT BusNumber FROM bus
    WHERE BusNumber NOT IN (SELECT DISTINCT BusId FROM schedule);
END;


------>ADD NEW ADMIN
--check if admin already exist
create procedure CheckIfAdminExist
	@name varchar(20), @pass varchar(20)
as
begin
select count(*) from admin
where username= @name
end

-->Insert into admin
create Procedure InsertIntoAdmin
	@name varchar(20), @pass varchar(20),@rank varchar(20)
as
begin
insert into Admin
values (@name,@pass,@rank)
end

-->> Return rank of the admin 
Create Procedure ReturnRankOfAdmin
	@name varchar(20)
as
begin
select Adminlevel from Admin
where username=@name
end


-->DISPLAY ADMINS TO MANAGE
Create view vw_displayForManageAdmin AS
select * from Admin
where AdminLevel = 'Sub'

create procedure displayForManageAdmin
as begin
select *from vw_displayForManageAdmin
end

-->>DELETE VALUES OF ADMIN
Create procedure DeleteSubAdminProfile
	@username varchar(20)
as
begin
Delete From admin 
where username= @username
end

-->> VIEW ALL PASSENGERS AS ADMIN
CREATE VIEW vw_ViewPassenger AS
SELECT passengerId, Name, ContactNumber, Email, Gender
FROM passenger;

create procedure ViewPassenger
as
begin
select *from vw_ViewPassenger
end
--> VIEW ALL RESERVATIONS AS ADMIN
create view vw_DisplayReservationAdmin as 
SELECT name, email, r.status, pa.Payment_method, pa.Amount
  FROM passenger p
  INNER JOIN reservation r ON r.PassengerId = p.passengerId
  INNER JOIN payment pa ON pa.ReservationId = r.ReservationId

CREATE PROCEDURE DisplayReservationAdmin
AS
BEGIN
  SELECT  *from vw_DisplayReservationAdmin
END;


--> view feedbacks of user
Create View vw_ViewFeedback AS
Select p.Name, s.BusId, Fare, s.RouteId, rating, Comments
from feedback f
inner join passenger p ON p.passengerId = f.passengerId
inner join schedule s ON s.ScheduleId = f.ScheduleId;

create procedure ViewFeedback AS
begin
select *from vw_ViewFeedback
end

--->CONTACT US PAGE INSERT
create Procedure InsertIntoContactus
	@first varchar(30), @last varchar(30), @email varchar(255), @comm text
as
begin 
insert into ContactUs
values(@first,@last,@email,@comm)
end

---->> DIsplay
create view vw_DisplayContactUs
as Select *from ContactUs

create procedure DisplayContactUs
as
begin
select*from vw_DisplayContactUs
end


--->> SHOW SEAT NUMBER BY ID
create procedure ShowBusSeatNumberByBusid
	@busid varchar(10)
as
begin
select Seat_number from reservation r
inner join schedule s on s.ScheduleId= r.ReservationId
inner join bus b on b.BusNumber=s.BusId
where b.BusNumber=@busid
end




--------------------------------------------------------->>> CREATING LOG TRIGGERS
create trigger trg_buslog
on bus
after insert, update
as
begin
    declare @AdminID varchar(25);
    declare @AdminName varchar(25);
    declare @Action varchar(10);
    declare @BusNumber varchar(7);

    select @AdminID = suser_sname();
    select @AdminName = user_name(); 

    if exists (select * from inserted)
    begin
        set @Action = 'Insert';
        select @BusNumber = BusNumber from inserted;
    end
    else
    begin
        set @Action = 'Update';
        select @BusNumber = BusNumber from deleted;
    end

    insert into logtable (AdminID, AdminName, EventOn, Action, AffectedID)
    values (@AdminID, @AdminName, 'Bus', @Action, @BusNumber);
end

create trigger trg_driverlog
on driver
after insert, update
as
begin
    declare @AdminID varchar(25);
    declare @AdminName varchar(25);
    declare @Action varchar(10);
    declare @DriverId int;

    select @AdminID = suser_sname();  
    select @AdminName = user_name(); 

    if exists (select * from inserted)
    begin
        set @Action = 'Insert';
        select @DriverId = DriverId from inserted;
    end
    else
    begin
        set @Action = 'Update';
        select @DriverId = DriverId from deleted;
    end

    insert into logtable (AdminID, AdminName, EventOn, Action, AffectedID)
    values (@AdminID, @AdminName, 'Driver', @Action, @DriverId);
end

create trigger trg_employeelog
on employee
after insert, update
as
begin
    declare @AdminID varchar(25);
    declare @AdminName varchar(25);
    declare @Action varchar(10);
    declare @EmployeeId int;

    select @AdminID = suser_sname(); 
    select @AdminName = user_name();  

    if exists (select * from inserted)
    begin
        set @Action = 'Insert';
        select @EmployeeId = Employee_id from inserted;
    end
    else
    begin
        set @Action = 'Update';
        select @EmployeeId = Employee_id from deleted;
    end

    insert into logtable (AdminID, AdminName, EventOn, Action, AffectedID)
    values (@AdminID, @AdminName, 'Employee', @Action, @EmployeeId);
end

select *from Admin