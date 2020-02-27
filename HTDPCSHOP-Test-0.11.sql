USE [master]
GO
USE MASTER
GO
-- KILL PROCESS
DECLARE @kill varchar(8000) = '';  
SELECT @kill = @kill + 'kill ' + CONVERT(varchar(5), session_id) + ';'  
FROM sys.dm_exec_sessions
WHERE database_id  = db_id('HTDShop')
EXEC(@kill);
GO

-- DROP DATABASE
DROP DATABASE IF EXISTS HTDShop
GO

CREATE DATABASE [HTDShop]
GO

USE HTDShop
GO

-- TABLE: CUSTOMER
CREATE TABLE [Customer](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[FirstName] [nvarchar](100) NOT NULL,
	[LastName] [nvarchar](255) NOT NULL,
	[Email] [varchar](255) NULL,
	[Phone] [varchar](20) NOT NULL,
	[Address] [nvarchar](255) NOT NULL,
	[Password] [varchar](100) NULL,
	[Gender] [bit] NULL,
	[Birthday] [date] NULL,
	[Point] [int] NULL,
	CONSTRAINT [PK_Customer] PRIMARY KEY ([Id])
)
GO
-- TABLE: CUSTOMER - END

-- TABLE: ROLE
CREATE TABLE [Role](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	CONSTRAINT [PK_Role] PRIMARY KEY ([Id])
)
GO
-- TABLE: ROLE - END

-- TABLE: RIGHTS DETAIL
CREATE TABLE [RightsDetail](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Tag] [varchar](100) NOT NULL,
	[Description] [text] NOT NULL,
	CONSTRAINT [PK_RightsDetail] PRIMARY KEY ([Id])
)
GO
-- TABLE: RIGHTS DETAIL - END

-- TABLE: ROLE RIGHTS
CREATE TABLE [RoleRights](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[RoleId] [int] NOT NULL,
	[RightsId] [int] NOT NULL,
	CONSTRAINT [PK_RoleRights] PRIMARY KEY ([Id]),
	CONSTRAINT [FK_RoleRights_RightsId_RightsDetail_Id] FOREIGN KEY([RightsId]) REFERENCES [RightsDetail] ([Id]),
	CONSTRAINT [FK_RoleRights_RoleId_Role_Id] FOREIGN KEY([RoleId]) REFERENCES [Role] ([Id])
)
GO
-- TABLE: ROLE RIGHTS - END

-- TABLE: STAFF
CREATE TABLE [Staff](
	[Username] [varchar](100) NOT NULL,
	[Password] [varchar](100) NOT NULL,
	[FirstName] [nvarchar](100) NOT NULL,
	[LastName] [nvarchar](255) NOT NULL,
	[Gender] [bit] NOT NULL,
	[Birthday] [date] NOT NULL,
	[Email] [varchar](255) NOT NULL,
	[Address] [nvarchar](255) NOT NULL,
	[Phone] [varchar](20) NOT NULL,
	[Image] [varchar](255) NOT NULL,
	[RoleId] [int] NOT NULL,
	CONSTRAINT [PK_Staff] PRIMARY KEY([Username]),
	CONSTRAINT [FK_Staff_RoleId_Role_Id] FOREIGN KEY([RoleId]) REFERENCES [Role] ([Id])
)
GO
-- TABLE: STAFF - END

-- TABLE: CATEGORY
CREATE TABLE [Category](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](50) NOT NULL,
	[Detail] [text] NOT NULL,
	CONSTRAINT [PK_Category] PRIMARY KEY ([Id])
)
GO
-- TABLE: CATEGORY - END

-- TABLE: PRODUCT
CREATE TABLE [Product](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[CateId] [int] NOT NULL,
	[Name] [nvarchar](255) NOT NULL,
	[Manufacturer] [nvarchar](100) NOT NULL,
	[Color] [nvarchar](255) NOT NULL,
	[Price] [float] NOT NULL,
	[Stock] [int] NOT NULL,
	[WarrantyPeriod] [int] NOT NULL,
	[Status] [int] NOT NULL,
	[Socket] [text] NULL,
	[Chipset] [varchar](100) NULL,
	[MemoryType] [varchar](100) NULL,
	[FormFactor] [varchar](100) NULL,
	[TDP] [int] NULL,
	[Interface] [varchar](100) NULL,
	[Memory] [int] NULL,
	[Series] [varchar](100) NULL,
	[Thread] [int] NULL,
	[Core] [int] NULL,
	[MemorySlot] [int] NULL,
	[StorageType] [varchar](100) NULL,
	[PSUWattage] [int] NULL,
	[MemoryModules] [int] NULL,
	[PSUFormFactor] [varchar](100) NULL,
	[ScreenSize] [float] NULL,
	[Resolution] [varchar](50) NULL,
	[Description] [text] NULL,
	CONSTRAINT [PK_Product] PRIMARY KEY ([Id]),
	CONSTRAINT [FK_Product_CateId_Cate_Id] FOREIGN KEY([CateId])REFERENCES [Category] ([Id])
)
GO
-- TABLE: PRODUCT - END

-- TABLE: PRODUCT IMAGE
CREATE TABLE [ProductImage](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ProductId] [int] NOT NULL,
	[ImagePath] [varchar](255) NOT NULL,
	[ThumbnailPath] [varchar](255) NULL,
	[MainImage] [bit] NOT NULL,
	CONSTRAINT [PK_ProductImage] PRIMARY KEY ([Id]),
	CONSTRAINT [FK_ProductImage_ProductId_Product_Id] FOREIGN KEY([ProductId])REFERENCES [Product] ([Id])
)
GO
-- TABLE: PRODUCT IMAGE - END

-- TABLE: PRODUCT COMMENT
CREATE TABLE [ProductComment](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ProductId] [int] NOT NULL,
	[CustomerId] [int] NOT NULL,
	[Content] [text] NOT NULL,
	[CreatedAt] [datetime] NOT NULL,
	CONSTRAINT [PK_ProductComment] PRIMARY KEY ([Id]),
	CONSTRAINT [FK_ProductComment_ProductId_Product_Id] FOREIGN KEY([ProductId]) REFERENCES [Product] ([Id]),
	CONSTRAINT [FK_ProductComment_CustomerId_Cusomter_Id] FOREIGN KEY(CustomerId)REFERENCES [Customer] ([Id])
)
GO
-- TABLE: PRODUCT COMMENT - END

-- TABLE: PREBUILT
CREATE TABLE [PreBuilt](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	[Detail] [text] NOT NULL,
	[CaseId] [int] NULL,
	[MonitorId] [int] NULL,
	[PSUId] [int] NULL,
	[StorageId] [int] NULL,
	[VGAId] [int] NULL,
	[MemoryId] [int] NULL,
	[CPUCoolerId] [int] NULL,
	[MotherBoardId] [int] NULL,
	[CPUId] [int] NULL,
	[CustomerId] [int] NULL,
	[StaffUsername] [varchar](100) NULL,
	[CreatedAt] [datetime] NOT NULL,
	CONSTRAINT [PK_PreBuilt] PRIMARY KEY ([Id]),
	CONSTRAINT [FK_PreBuilt_CaseId_Product_Id] FOREIGN KEY([CaseId]) REFERENCES [Product] ([Id]),
	CONSTRAINT [FK_PreBuilt_CPUCoolerId_Product_Id] FOREIGN KEY([CPUCoolerId]) REFERENCES [Product] ([Id]),
	CONSTRAINT [FK_PreBuilt_CPUId_Product_Id] FOREIGN KEY([CPUId]) REFERENCES [Product] ([Id]),
	CONSTRAINT [FK_PreBuilt_MemoryId_Product_Id] FOREIGN KEY([MemoryId]) REFERENCES [Product] ([Id]),
	CONSTRAINT [FK_PreBuilt_MonitorId_Product_Id] FOREIGN KEY([MonitorId]) REFERENCES [Product] ([Id]),
	CONSTRAINT [FK_PreBuilt_MotherBoardId_Product_Id] FOREIGN KEY([MotherBoardId]) REFERENCES [Product] ([Id]),
	CONSTRAINT [FK_PreBuilt_PSUId_Product_Id] FOREIGN KEY([PSUId]) REFERENCES [Product] ([Id]),
	CONSTRAINT [FK_PreBuilt_StorageId_Product_Id] FOREIGN KEY([StorageId]) REFERENCES [Product] ([Id]),
	CONSTRAINT [FK_PreBuilt_VGAId_Product_Id] FOREIGN KEY([VGAId]) REFERENCES [Product] ([Id]),
	CONSTRAINT [FK_PreBuilt_StaffUsername_Staff_Username] FOREIGN KEY([StaffUsername]) REFERENCES [Staff] ([Username]),
	CONSTRAINT [FK_PreBuilt_CustomerId_Customer_Id] FOREIGN KEY([CustomerId])REFERENCES [Customer] ([Id])

)
GO
-- TABLE: PREBUILT - END

-- TABLE: PREBUILT IMAGE
CREATE TABLE [PreBuiltImage](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[PreBuiltId] [int] NOT NULL,
	[Path] [nvarchar](255) NOT NULL,
	CONSTRAINT [PK_PreBuiltImage] PRIMARY KEY ([Id]),
	CONSTRAINT [FK_PreBuiltImage_PreBuiltId_PreBuilt_Id] FOREIGN KEY([PreBuiltId]) REFERENCES [PreBuilt] ([Id])
)
GO
-- TABLE: PREBUILT IMAGE - END

-- TABLE: PRODUCT COMMENT REPLY
CREATE TABLE [ProductCommentReply](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ProductCommentId] [int] NOT NULL,
	[CustomerId] [int] NULL,
	[StaffUsername] [varchar](100) NULL,
	[Content] [text] NOT NULL,
	[CreatedAt] [datetime] NOT NULL,
	CONSTRAINT [PK_ProductCommentReply] PRIMARY KEY ([Id]),
	CONSTRAINT [FK_ProductCommentReply_ProductCommentId_ProductComment_Id] FOREIGN KEY([ProductCommentId]) REFERENCES [ProductComment] ([Id]),
	CONSTRAINT [FK_ProductCommentReply_StaffUsername_Staff_Username] FOREIGN KEY([StaffUsername]) REFERENCES [Staff] ([Username]),
	CONSTRAINT [FK_ProductCommentReply_CustomerId_User_Id] FOREIGN KEY([CustomerId]) REFERENCES [Customer] ([Id])
)
GO
-- TABLE: PRODUCT COMMENT REPLY - END

-- TABLE: PREBUILT RATING
CREATE TABLE [PreBuiltRating](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[PreBuiltId] [int] NOT NULL,
	[CustomerId] [int] NOT NULL,
	[Comment] [text] NOT NULL,
	[Rating] [float] NOT NULL,
	[CreatedAt] [datetime] NOT NULL,
	CONSTRAINT [PK_PreBuiltRating] PRIMARY KEY ([Id]),
	CONSTRAINT [FK_PreBuiltRating_PreBuiltId_PreBuilt_Id] FOREIGN KEY([PreBuiltId]) REFERENCES [PreBuilt] ([Id]),
	CONSTRAINT [FK_PreBuiltRating_CustomerId_Customer_Id] FOREIGN KEY([CustomerId]) REFERENCES [Customer] ([Id])
)
GO
-- TABLE: PREBUILT RATING - END

-- TABLE: PROMOTION DETAIL
CREATE TABLE [PromotionDetail](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	[Detail] [text] NOT NULL,
	[Image] [varchar](255) NULL,
	[Target] [int] NOT NULL,
	[StartDate] [datetime] NULL,
	[EndDate] [datetime] NULL,
	[IsAlways] [bit] NOT NULL,
	[IsDisabled] [bit] NOT NULL,
	CONSTRAINT [PK_PromotionDetail] PRIMARY KEY ([Id])
)
GO
-- TABLE: PROMOTION DETAIL - END

-- TABLE: PROMOTION
CREATE TABLE [Promotion](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[PromotionDetailId] [int] NOT NULL,
	[CategoryId] [int] NULL,
	[ProductId] [int] NULL,
	[PreBuiltTarget] [int] NULL,
	[LimitedQuantity] [int] NULL,
	[MinQuantity] [int] NULL,
	[MaxQuantity] [int] NULL,
	[QuantityLeft] [int] NULL,
	[Percentage] [float] NULL,
	[ExactSaleOff] [float] NULL,
	[MaxSaleOff] [float] NULL,
	CONSTRAINT [PK_Promotion] PRIMARY KEY ([Id]),
	CONSTRAINT [FK_Promotion_CategoryId_Category_Id] FOREIGN KEY([CategoryId]) REFERENCES [Category] ([Id]),
	CONSTRAINT [FK_Promotion_ProductId_Product_Id] FOREIGN KEY([ProductId]) REFERENCES [Product] ([Id]),
	CONSTRAINT [FK_Promotion_PromotionDetailId_PromotionDetail_Id] FOREIGN KEY([PromotionDetailId]) REFERENCES [PromotionDetail] ([Id])
)
GO
-- TABLE: PROMOTION - END

-- TABLE: IMAGE SLIDE
CREATE TABLE ImageSlide(
	Id INT IDENTITY(1,1) NOT NULL,
	[Image] TEXT,
	[Order] INT,
	Title TEXT,
	[Description] TEXT,
	Link TEXT,
	[Status] BIT NOT NULL,
	CONSTRAINT [PK_ImageSlide] PRIMARY KEY ([Id])
)
-- TABLE: IMAGE SLIDE - END

-- TABLE: ORDER
CREATE TABLE [Order](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[CustomerId] [int] NOT NULL,
	[PurchasedMethod] [int] NOT NULL,
	[PaymentMethod] [int] NOT NULL,
	[PaymentStatus] [bit] NOT NULL,
	[PaidDate] [datetime] NULL,
	[OrderStatus] [int] NOT NULL,
	[OrderDate] [datetime] NOT NULL,
	[CancelledDate] [datetime] NULL,
	CONSTRAINT [PK_Order] PRIMARY KEY ([Id]),
	CONSTRAINT [FK_Order_CustomerId_Customer_Id] FOREIGN KEY([CustomerId]) REFERENCES [Customer] ([Id])
)
GO
-- TABLE: ORDER - END

-- TABLE: ORDER DETAIL
CREATE TABLE [OrderDetail](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[OrderId] [int] NOT NULL,
	[ProductId] [int] NOT NULL,
	[Quantity] [int] NOT NULL,
	[Price] [float] NOT NULL,
	CONSTRAINT [PK_OrderDetail] PRIMARY KEY ([Id]),
	CONSTRAINT [FK_OrderDetail_OrderId_Order_Id] FOREIGN KEY([OrderId]) REFERENCES [Order] ([Id]),
	CONSTRAINT [FK_OrderDetail_ProductId_Product_Id] FOREIGN KEY([ProductId])REFERENCES [Product] ([Id])
)
GO
-- TABLE: ORDER DETAIL - END

-- TABLE: DELIVERY
CREATE TABLE [Delivery](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[OrderId] [int] NOT NULL,
	[DeliveryStaff] [varchar](100) NOT NULL,
	[DeliveryStatus] [int] NOT NULL,
	[AssignedDate] [datetime] NOT NULL,
	[DelayedDate] [datetime] NULL,
	[DeliveredDate] [datetime] NULL,
	[CancelledDate] [datetime] NULL,
	CONSTRAINT [PK_Delivery] PRIMARY KEY ([Id]),
	CONSTRAINT [FK_Delivery_DeliveryStaff_Staff_Username] FOREIGN KEY([DeliveryStaff]) REFERENCES [Staff] ([Username]),
	CONSTRAINT [FK_Delivery_OrderId_Order_Id] FOREIGN KEY([OrderId]) REFERENCES [Order] ([Id])
)
GO
-- TABLE: DELIVERY - END



USE HTDShop
GO

-- CUSTOMER
INSERT [Customer] ([FirstName], [LastName], [Email], [Phone], [Address], [Password], [Gender], [Birthday], [Point]) 
VALUES	-- 1
		(N'Dung', N'Nguyen', N'nguyendung@mail.com', N'09090900000', N'123 Abc HCM City', N'nguyendung', 1, CAST(N'1993-01-02' AS Date), 300),
		-- 2
		(N'Dan', N'Ortiz', N'dan.ortiz@example.com', N'2539393807', N'4008 Spring Hill Rd', NULL, NULL, NULL, NULL),
		-- 3
		(N'Hai', N'Vo', N'vohai@gmail.com', N'0118880056', N'234 Bcd District 2 HCM City', N'vohai', 1, CAST(N'1993-11-08' AS Date), 1200),
		-- 4
		(N'Mae', N'Wheeler', N'mae.wheeler@example.com', N'3357412570', N'9557 Poplar Dr', NULL, NULL, NULL, NULL),
		-- 5
		(N'Jordan', N'Mt', N'mtjordan@mail.com', N'1233355848', N'345 Cde L.A', N'mtjordan', 1, CAST(N'1988-11-02' AS Date), 100),
		-- 6
		(N'Henry', N'Michele', N'michele.henry@example.com', N'4437689768', N'5651 Plum St', NULL, NULL, NULL, NULL),
		-- 7
		(N'Nicole', N'Carlson', N'nicolecarlson@mail.com', N'3826334737', N'437 N Stelling Rd', N'nicole', 0, CAST(N'1990-04-22' AS Date), 0),
		-- 8
		(N'Bao', N'Van', N'vanbao@gmail.com', N'0201121021', N'456 Def', N'vanbao', 1, CAST(N'1992-07-07' AS Date), 25),
		-- 9
		(N'Thuy', N'Tran', N'thuytran@gmail.com', N'123123123444', N'567 Efg', N'thuytran', 0, CAST(N'1990-03-18' AS Date), 0),
		-- 10
		(N'Adam', N'Nguyen', N'adamnguyen@gmail.com', N'456456456456', N'678 Fgh', N'adamnguyen', 1, CAST(N'1985-05-15' AS Date), 5)
GO
-- CUSTOMER - END

-- ROLE
INSERT [Role] ([Name]) 
VALUES	-- 1
		(N'Administrator'), 
		-- 2
		(N'General Staff'), 
		-- 3
		(N'Shipper'), 
		-- 4
		(N'Customer Care')
-- ROLE - END

-- RIGHT DETAIL
INSERT INTO RightsDetail (Tag, [Description])
VALUES	-- 1					2					3					4
		('product_read', ''), ('product_add', ''),('product_edit', ''),('product_delete', ''),
		-- 5					6					7						8
		('prebuilt_read', ''), ('prebuilt_add', ''),('prebuilt_edit', ''),('prebuilt_delete', ''),
		-- 9					10					11					12
		('comment_read', ''), ('comment_add', ''),('comment_edit', ''),('comment_delete', ''),
		-- 13					14				15					16
		('order_read', ''), ('order_add', ''),('order_edit', ''),('order_delete', ''),
		-- 17					18						19					20
		('customer_read', ''), ('customer_add', ''),('customer_edit', ''),('customer_delete', ''),
		-- 21					22						23						24
		('promotion_read', ''), ('promotion_add', ''),('promotion_edit', ''),('promotion_delete', ''),
		-- 25						26					27							28
		('imageslide_read', ''), ('imageslide_add', ''),('imageslide_edit', ''),('imageslide_delete', ''),
		-- 29					30				31					32
		('staff_read', ''), ('staff_add', ''),('staff_edit', ''),('staff_delete', ''),
		-- 33				34				35					36
		('role_read', ''), ('role_add', ''),('role_edit', ''),('role_delete', ''),
		-- 37
		('revenue_read', ''),
		-- 38					39
		('delivery_read', ''), ('delivery_edit', '')
GO
-- RIGHT DETAIL - END

-- ROLE RIGHTS
INSERT [dbo].[RoleRights] ([RoleId], [RightsId]) 
VALUES	-- Administrator
		(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),(1, 6),  (1, 7), (1, 8), (1, 9), (1, 10), 
		(1, 11), (1, 12), (1, 13), (1, 14), (1, 15),(1, 16),  (1, 17), (1, 18), (1, 19), (1, 20), 
		(1, 21), (1, 22), (1, 23), (1, 24), (1, 25),(1, 26),  (1, 27), (1, 28), (1, 29), (1, 30), 
		(1, 31), (1, 32), (1, 33), (1, 34), (1, 35),(1, 36),  (1, 37), (1, 38), (1, 39),
		-- General Staff
		(2, 1), (2, 2), (2, 3), (2, 4), (2, 5),(2, 6),  (2, 7), (2, 8), (2, 9), (2, 10), 
		(2, 11), (2, 12), (2, 13), (2, 14), (2, 15),(2, 16),  (2, 17), (2, 18), (2, 19), (2, 20), 
		(2, 21), (2, 22), (2, 23), (2, 24), (2, 25),(2, 26),  (2, 27), (2, 28), (2, 38), (2, 39),
		-- Shipper
		(3, 13), (3, 38), (3, 39),
		-- Customer Care
		(4, 1), (4, 5), (4, 9), (4, 10), (4, 11), (4, 12), 
		(4, 13), (4, 14), (4, 15),(4, 16),  (4, 17), (4, 18), (4, 19), (4, 20), 
		(4, 21), (4, 38)
GO
-- ROLE RIGHTS - END

-- STAFF
INSERT [Staff] ([Username], [Password], [LastName], [FirstName], [Gender], [Birthday], [Email], [Address], [Phone], [Image], [RoleId]) 
VALUES	-- 1
		(N'admin', N'admin', N'Mr', N'Admin', 0, CAST(N'1993-01-01' AS Date), N'admin@gmail.com', N'HN', N'007007007', N'staff/admin.jpg', 1),
		-- 2
		(N'ducdung', N'dung123', N'Duc', N'Dung', 0, CAST(N'1993-01-04' AS Date), N'dung@gmail.com', N'HN', N'0888333444', N'staff/dung.png', 1),
		-- 3
		(N'haithien', N'thien123', N'Hai', N'Thien', 0, CAST(N'1993-01-02' AS Date), N'thien@gmail.com', N'HCM', N'0888111222', N'staff/thien.png', 2),
		-- 4
		(N'viethai', N'hai123', N'Viet', N'Hai', 0, CAST(N'1993-01-03' AS Date), N'hai@gmail.com', N'HCM', N'0888222333', N'staff/hai.png', 2),
		-- 5
		(N'shipper', N'shipper', N'Mr', N'Shipper', 0, CAST(N'1993-01-03' AS Date), N'shipper@gmail.com', N'HCM', N'0888222333', N'staff/shipper.png', 3),
		-- 6
		(N'customercare', N'customercare', N'Ms', N'Customer Care', 0, CAST(N'1993-01-03' AS Date), N'customercare@gmail.com', N'HCM', N'0888222333', N'staff/customercare.jpg', 4)
GO
-- STAFF - END

-- CATEGORY
INSERT INTO [Category] ([Name], [Detail]) 
VALUES	--1 
		(N'CPU', N'Central processing unit.'), 
		--2
		(N'Motherboard', N'Motherboard / Mainboard'), 
		--3
		(N'GPU', N'Graphics processing unit'),
		--4
		(N'Memory', N'RAM (Random-access memory).'),
		--5
		(N'PSU', N'Power supply unit.'),
		--6
		(N'Storage', N'Storage (HDD, SSD,...).'),
		--7
		(N'CPU Cooler', N'Cooler for CPU'),
		--8
		(N'Case', N'Case.'),
		--9
		(N'Monitor', N'Monitor.')
GO
-- CATEGORY - END

-- PRODUCT - CPU
INSERT INTO [Product] ([CateId], [Name], [Manufacturer], [Color], [Price], [Stock], [WarrantyPeriod], [Status], 
						[Socket], [Chipset], [MemoryType], [FormFactor], [TDP], [Interface], [Memory], 
						[Series], [Thread], [Core], [MemorySlot], [StorageType], [PSUWattage], [MemoryModules], 
						[PSUFormFactor], [ScreenSize], [Resolution], [Description])  
VALUES	--1
		(1, 'AMD Ryzen 5 3600 3.6 GHz 6-Core', 'AMD', 'None', 174.99, 50, 36, 1,
		'AM4', NULL, NULL, NULL, 65, NULL, NULL,
		'AMD Ryzen 5', 12, 6, NULL, NULL, NULL, NULL,
		NULL, NULL, NULL, '<ul><li>The world''s most advanced processor in the desktop PC gaming segment</li>
						<li>Can deliver ultra fast 100+ FPS performance in the world''s most popular games</li>
						<li>6 Cores and 12 processing threads, bundled with the Quiet AMD Wraith Stealth cooler</li>
						<li>4.2 GHz Max Boost, unlocked for overclocking, 35 MB of game Cache, ddr4 3200 support</li>
						<li>For the advanced socket AM4 platform, can support PCIe 4.0 on x570 motherboards</li></ul>'),
		--2
		(1, 'AMD Ryzen 5 2600 3.4 GHz 6-Core', 'AMD', 'None', 119.99, 50, 36, 1,
		'AM4', NULL, NULL, NULL, 65, NULL, NULL,
		'AMD Ryzen 5', 12, 6, NULL, NULL, NULL, NULL,
		NULL, NULL, NULL, '<ul><li>Frequency: 3.9 GHz Max Boost; Includes Wraith Stealth Cooler</li>
						<li>19MB of Combined Cache. Maximum temperature: 95 degree C; CMOS: 12nm FinFET</li>
						<li>Compatibility : Windows 10; 64 Bit Edition , RHEL x86 64 Bit , Ubuntu x86 64 Bit</li>
						<li>4.2 GHz Max Boost, unlocked for overclocking, 35 MB of game Cache, ddr4 3200 support</li>
						<li>Supported technologies are amd storemi technology, amd sensemi technology, amd ryzen master utility and amd ryzen vr ready premium</li></ul>'),
		--3
		(1, 'AMD Ryzen 7 3700X 3.6 GHz 8-Core', 'AMD', 'None', 309.99, 30, 36, 1,
			'AM4', NULL, NULL, NULL, 65, NULL, NULL,
			'AMD Ryzen 7', 16, 8, NULL, NULL, NULL, NULL,
			NULL, NULL, NULL, '<ul><li>The world''s most advanced processor in the desktop PC gaming segment</li>
						<li>Can deliver ultra fast 100+ FPS performance in the world''s most popular games</li>
							<li>8 Cores and 16 processing threads, bundled with the AMD Wraith Prism cooler with color controlled LED support</li>
							<li>4.4 GHz Max Boost, unlocked for overclocking, 36 MB of game Cache, ddr 3200 support</li>
							<li>For the advanced socket AM4 platform, can support PCIe 4.0 on x570 motherboards</li></ul>'),
		--4
		(1, 'AMD Ryzen 9 3900X 3.8 GHz 12-Core', 'AMD', 'None', 469.99, 10, 36, 2,
			'AM4', NULL, NULL, NULL, 105, NULL, NULL,
			'AMD Ryzen 9', 24, 12, NULL, NULL, NULL, NULL,
			NULL, NULL, NULL, '<ul><li>The world''s most advanced processor in the desktop PC gaming segment</li>
						<li>Can deliver ultra fast 100+ FPS performance in the world''s most popular games</li>
							<li>12 Cores and 24 processing threads, bundled with the AMD Wraith Prism cooler with color controlled LED support</li>
							<li>4.6 GHz Max Boost, unlocked for overclocking, 70 MB of game Cache, ddr 3200 support</li>
							<li>For the advanced socket AM4 platform, can support PCIe 4.0 on x570 motherboards</li></ul>'),
		--5
		(1, 'Intel Core i7-9700K 3.6 GHz 8-Core', 'Intel', 'None', 399.89, 20, 36, 1,
			'LGA1151', NULL, NULL, NULL, 95, NULL, NULL,
			'Intel Core i7', 8, 8, NULL, NULL, NULL, NULL,
			NULL, NULL, NULL, '<ul><li>3.60 GHz up to 4.90 GHz / 12 MB Cache</li>
							<li>Compatible only with Motherboards based on Intel 300 Series Chipsets</li>
							<li>Intel Optane Memory Supported</li>
							<li>Intel UHD Graphics 630</li>
							<li>Cooling device not included - Processor Only</li></ul>'),
		--6
		(1, 'Intel Core i5-9400F 2.9 GHz 6-Core', 'Intel', 'None', 165, 50, 36, 1,
			'LGA1151', NULL, NULL, NULL, 65, NULL, NULL,
			'Intel Core i5', 6, 6, NULL, NULL, NULL, NULL,
			NULL, NULL, NULL, '<ul><li>2.90 GHz up to 4.10 GHz Max Turbo Frequency/ 9 MB Cache, Bus Speed: 8 GT/s DMI3</li>
							<li>Compatible only with Motherboards based on Intel 300 Series Chipsets: Intel B360 Chipset, Intel H370 Chipset, Intel H310 Chipset, Intel Q370 Chipset, Intel Z390 Chipset, Intel Z370 Chipset</li>
							<li>Discrete GPU required No integrated graphics</li>
							<li>Intel Optane Memory supported</li></ul>'),
		--7
		(1, 'Intel Core i3-8100 3.6 GHz Quad-Core', 'Intel', 'None', 119.99, 60, 36, 1,
			'LGA1151', NULL, NULL, NULL, 65, NULL, NULL,
			'Intel Core i3', 4, 4, NULL, NULL, NULL, NULL,
			NULL, NULL, NULL, '<ul><li>3.60 GHz / 6 MB Cache</li>
							<li>Compatible only with Motherboards based on Intel 300 Series Chipsets</li>
							<li>Intel UHD Graphics 630</li>
							<li>Intel Optane Memory supported</li></ul>'),
		--8
		(1, 'AMD FX-8350 4 GHz 8-Core', 'AMD', 'None', 269.99, 5, 36, 1,
		'AM4', NULL, NULL, NULL, 125, NULL, NULL,
		'AMD FX', 8, 8, NULL, NULL, NULL, NULL,
		NULL, NULL, NULL, '<ul><li>8 MB level 3 cache architecture to provide versatile performance and fast access to frequently used data</li>
						<li>Built on 32 nm technology and uses a high-k metal gate to enable higher performing transistors on the same node reducing leakage of currents</li></ul>'),
		--9
		(1, 'Intel Core i7-3770 3.4 GHz Quad-Core', 'Intel', 'None', 190, 0, 36, 3,
			'LGA1155', NULL, NULL, NULL, 77, NULL, NULL,
			'Intel Core i7', 4, 4, NULL, NULL, NULL, NULL,
			NULL, NULL, NULL, ''),
		--10
		(1, 'AMD FX-6300 3.5 GHz 6-Core', 'AMD', 'None', 0, 0, 36, 3,
			'AM3+', NULL, NULL, NULL, 95, NULL, NULL,
			'AMD FX', 6, 6, NULL, NULL, NULL, NULL,
			NULL, NULL, NULL, '')
GO
-- PRODUCT - Motherboard
INSERT INTO [Product] ([CateId], [Name], [Manufacturer], [Color], [Price], [Stock], [WarrantyPeriod], [Status], 
						[Socket], [Chipset], [MemoryType], [FormFactor], [TDP], [Interface], [Memory], 
						[Series], [Thread], [Core], [MemorySlot], [StorageType], [PSUWattage], [MemoryModules], 
						[PSUFormFactor], [ScreenSize], [Resolution], [Description])
VALUES	--11
		(2, N'MSI B450 TOMAHAWK ATX AM4', N'MSI', N'Silver', 111.99, 50, 12, 1, 
		N'AM4', N'AMD B450', N'DDR4', N'ATX', NULL, NULL, NULL,
		NULL, NULL, NULL, 4, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Support AMD Ryzen 1st and 2nd Generation / Ryzen with Radeon Vega Graphics Processors for Socket AM4</li>
						<li>Supports 64GB Dual Channel DDR4 Memory 1866/ 2133/ 2400/ 2667 MHz by JEDEC, and 2667/ 2800/ 2933/ 3000/ 3066/ 3200/ 3466 MHz by A XMP OC MODE</li>
						<li>Supports 2 Way AMD Crossfire Technology</li>
						<li>In Game Weapons: Game Boost, GAMING Hotkey, X Boost; EZ Debug LED: Easiest way to troubleshoot</li>
						<li>GAMING CERTIFIED: 24 hour on and offline game and motherboard testing by eSports players.LAN Chipset:Realtek 8111H.Maximum shared memory of 2048 MB</li></ul>'),
		--12
		(2, N'ASRock B450M PRO4 Micro ATX AM4', N'ASRock', N'Silver', 74.98, 70, 12, 1, 
		N'AM4', N'AMD B450', N'DDR4', N'Micro ATX', NULL, NULL, NULL,
		NULL, NULL, NULL, 4, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Socket AM4, Digi Power design, 9 Power Phase design, Supports 105W Water Cooling (Pinnacle Ridge); Supports 95W Water Cooling (Summit Ridge); Supports 65W Water Cooling (Raven Ridge)</li>
						<li>Chipset: AMD Promontory B450</li>
						<li>Supports 2 Way AMD Crossfire Technology</li>
						<li>Memory: 4x DDR4-3200+(OC)/2933/2667/2400/2133 DIMM Slots, Dual Channel, ECC, Non-ECC, Unbuffered, Max Capacity of 64GB (AMD Ryzen series CPUs (Pinnacle Ridge))</li>
						<li>Slots: 2x PCI-Express 3.0 x16 Slots (one runs at x8), 2x PCI-Express 2.0 x16 Slots (two run at x4), 1x PCI-Express 2.0 x1 Slot</li>
						<li>SATA: 4x SATA3 Ports, Support RAID 0, 1, 10, NCQ, AHCI and Hot Plug</li></ul>'),
		--13
		(2, N'Gigabyte GA-A320M-S2H Micro ATX AM4', N'Gigabyte', N'Black', 54.99, 100, 12, 1, 
		N'AM4', N'AMD A320', N'DDR4', N'Micro ATX', NULL, NULL, NULL,
		NULL, NULL, NULL, 2, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Supports AMD Ryzen & 7th Generation A series/Athlon Processors</li>
						<li>Dual Channel Non ECC Unbuffered DDR4, 2 DIMMs</li>
						<li>Ultra Fast PCIe Gen3 x4 M.2 with PCIe NVMe & SATA mode support</li>
						<li>High Quality Audio Capacitors and Audio Noise Guard</li>
						<li>Realtek Gigabit LAN with cFosSpeed Internet Accelerator Software</li></ul>'),
		--14
		(2, N'MSI B450M GAMING PLUS Micro ATX AM4', N'MSI', N'Red', 89.97, 80, 12, 1, 
		N'AM4', N'AMD B450', N'DDR4', N'Micro ATX', NULL, NULL, NULL,
		NULL, NULL, NULL, 2, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Support AMD Ryzen 1st and 2nd Generation / Ryzen with Radeon Vega Graphics Processors for Socket AM4</li>
						<li>Supports 32GB Dual Channel DDR4 Memory 1866/ 2133/ 2400/ 2667 MHz by JEDEC, and 2667/ 2800/ 2933/ 3000/ 3066/ 3200/ 3466 MHz by A-XMP OC MODE</li>
						<li>VR Ready: Automatically optimizes your system for VR usage, pushing for maximum performance</li>
						<li>In-Game Weapons: Game Boost, GAMING Hotkey, X-Boost</li>
						<li>GAMING CERTIFIED: 24-hour on- and offline game and motherboard testing by eSports players</li></ul>'),
		--15
		(2, N'Asus ROG MAXIMUS XI HERO (WI-FI) ATX LGA1151', N'Asus', N'Black', 279.99, 30, 12, 1, 
		N'LGA1151', N'Intel Z390', N'DDR4', N'ATX', NULL, NULL, NULL,
		NULL, NULL, NULL, 4, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Designed for 9th 8th Generation Intel Core processors maximize connectivity, speed with Dual M.2, USB 3.1 Gen 2, on board 802.11AC Wi Fi and ASUS optimum II for better DRAM overclocking stability</li>
						<li>Revamped 5 way Optimization over clocks Intelligently based on smart prediction and thermal telemetry while FanXpert 4 delivers dynamic system cooling</li>
						<li>Aura Sync RGB lighting with addressable headers features a nearly endless spectrum of colors with the ability to synchronize effects across an ever expanding ecosystem of AURA Sync enabled products</li>
						<li>Pre mounted I/O shield ensures streamlined installation and represents ROG''s attention to detail and quality</li></ul>'),
		--16
		(2, N'Gigabyte B365M DS3H Micro ATX LGA1151', N'Gigabyte', N'Black', 74.69, 80, 12, 1, 
		N'LGA1151', N'Intel B365', N'DDR4', N'Micro ATX', NULL, NULL, NULL,
		NULL, NULL, NULL, 4, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Supports 9th and 8th Gen Intel Core processors</li>
						<li>Dual Channel Non-ECC Unbuffered DDR4</li>
						<li>High quality Audio Capacitors and Audio Noise Guard with LED Trace Path Lighting</li>
						<li>Ultra-fast M.2 with PCIe Gen3 x4 & SATA interface</li></ul>'),
		--17
		(2, N'Asus PRIME H310M-E R2.0 Micro ATX LGA1151', N'Asus', N'Black', 56.07, 100, 12, 1, 
		N'LGA1151', N'Intel H310', N'DDR4', N'Micro ATX', NULL, NULL, NULL,
		NULL, NULL, NULL, 2, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Designed exclusively for 8th generation Intel Core processors to maximize connectivity and speed with an integrated M.2 slot</li>
						<li>5x Protection III Hardware-level safeguards provide component longevity and reliability</li>
						<li>Fan pert delivers advanced fan controls for dynamic cooling</li>
						<li>Asus optimized improves memory stability and performance by improving trace isolation between PCB layers to maintain signal integrity even at higher frequencies</li></ul>'),
		--18
		(2, N'ASRock B250 Pro4 ATX LGA1151', N'ASRock', N'White', 119.99, 17, 12, 1, 
		N'LGA1151', N'Intel B250', N'DDR4', N'ATX', NULL, NULL, NULL,
		NULL, NULL, NULL, 4, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Memory: 4x DDR4-2400/2133 DIMM Slots, Dual Channel, Non-ECC, Unbuffered, Max Capacity of 64GB</li>
						<li>Slots: 2x PCI-Express 3.0 x16 Slots (one runs at x4), 3x PCI-Express 3.0 x1 Slots (Flexible PCI-Express), 1x PCI Slot</li>
						<li>Multi-Graphics: Supports AMD Quad CrossFireX, CrossFireX Technology</li>
						<li>SATA: 6x SATA3 Ports, Support NCQ, AHCI and Hot Plug</li></ul>')
GO
-- PRODUCT - VGA
INSERT INTO [Product] ([CateId], [Name], [Manufacturer], [Color], [Price], [Stock], [WarrantyPeriod], [Status], 
						[Socket], [Chipset], [MemoryType], [FormFactor], [TDP], [Interface], [Memory], 
						[Series], [Thread], [Core], [MemorySlot], [StorageType], [PSUWattage], [MemoryModules], 
						[PSUFormFactor], [ScreenSize], [Resolution], [Description])
VALUES	--19
		(3, N'Asus GeForce RTX 2080 Ti 11 GB ROG Strix Gaming OC', N'Asus', N'Black', 1229.95, 50, 12, 1, 
		NULL, N'GeForce RTX 2080 Ti', N'GDDR6', NULL, 250, N'PCIe x16', 11, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Powered by NVIDIA Turing with 1665 MegaHertz Boost Clock (OC Mode), 4352 CUDA cores and overclocked 11 GigaBytes GDDR6 memory in a 2.7 slot form Factor; Supports up to 4 monitors with Display Port 1. 4, HDMI 2. 0 and a VR headset via USB Type C ports</li>
						<li>Auto Extreme and Max Contact Technology deliver premium quality and reliability with aerospace grade Super Alloy Power II components while maximizing heat sink contact</li>
						<li>ASUS Aura Sync RGB lighting features a nearly endless spectrum of colors with the ability to synchronize effects across an ever expanding ecosystem of AURA Sync enabled products</li>
						<li>GPU Tweak II makes monitoring performance and streaming in real time easier than ever, and includes additional software like Game Booster, X Split Game caster, WT Fast and Quantum Cloud</li></ul>'),
		--20
		(3, N'MSI GeForce GTX 1660 Ti 6 GB VENTUS XS OC', N'MSI', N'White', 274.99, 80, 12, 1, 
		NULL, N'GeForce GTX 1660 Ti', N'GDDR6', NULL, 120, N'PCIe x16', 6, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Boost Clock: 1830 MHz; Core Clocks: 1770 MHz</li>
						<li>Memory Interface: 192-bit</li>
						<li>Output: DisplayPort x 3 (V1. 4)/ HDMI 2. 0B x 1</li>
						<li>RECOMMENDED PSU: 450 W</li></ul>'),
		--21
		(3, N'XFX Radeon RX 580 8 GB GTS XXX ED', N'XFX', N'Black', 159.99, 80, 12, 1, 
		NULL, N'Radeon RX 580', N'GDDR5', NULL, 185, N'PCIe x16',  8, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>The XFX RX 580 series graphics card feature the latest Polaris architecture which includes the 4th Gen GCN graphics cores, a brand new display engine, new multimedia cores, all on the revolutionary Next FinFET 14 process technology for enhanced performance and efficiency</li>
						<li>Equipped with XFX Double Dissipation Cooling Technology for optimal cooling and performanc. Minimum power requirement is 500 watts. Memory Clock True:8.0GHz, Boost OC+:8.1GHz</li>
						<li>Multiple Factory GPU Overclocked Settings - 1366MHz True Clock and 1386MHz OC+</li>
						<li>AMD VR Ready Premium -Experience the new generation of compelling Virtual Reality content with the Radeon RX GTS graphics card paired with the leading VR headsets. The Radeon RX GTS graphics card coupled with AMD LiquidVR technology delivers a virtually stutter-free, low latency experience, essential for remarkable Virtual Reality environments</li></ul>'),
		--22
		(3, N'MSI GeForce GTX 1050 Ti 4 GB', N'MSI', N'Black', 155.99, 57, 12, 1, 
		NULL, N'GeForce GTX 1050 Ti', N'GDDR5', NULL, 75, N'PCIe x16',  4, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Video Memory: 4GB GDDR5</li>
						<li>Memory Interface: 128-bit. Core Clocks : 1455 MHz / 1341 MHz</li>
						<li>Bus: PCI-Express 3.0 x16</li>
						<li>Form Factor: ATX. Power consumption 75 W, Recommended PSU 300 W</li></ul>'),
		--23
		(3, N'Sapphire Radeon RX 5700 XT 8 GB PULSE', N'Sapphire', N'Black', 409.99, 57, 12, 1, 
		NULL, N'Radeon RX 5700 XT', N'GDDR6', NULL, 241, N'PCIe x16',  8, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Powered by 7nm Radeon rdna architecture</li>
						<li>Accelerated game fidelity with Radeon image sharpening & fidelityfx</li>
						<li>POWER INDICATORS-The smart power LED indicators alert the player when any PCI-E power supply is abnormal.</li>
						<li>COMPOSITE HEAT-PIPE-The composite heat-pipe combines thermal conductivity and phase transition to efficiently manage the heat transfer between two solid interfaces which increases cooling capacity.</li></ul>'),
		--24
		(3, N'MSI GeForce GT 710 1 GB', N'MSI', N'Black', 40.99, 0, 12, 3, 
		NULL, N'GeForce GT 710', N'GDDR3', NULL, 19, N'PCIe x16',  1, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>1 HDMI, 1 DL-DVI-D and 1 VGA (D-sub)</li>
						<li>Low-profile Design</li>
						<li>Fanless and Noise less GPU Heatsink</li></ul>'),
		--25
		(3, N'XFX Radeon HD 5450 1 GB ', N'XFX', N'Black', 135.59, 7, 12, 3, 
		NULL, N'Radeon HD 5450', N'GDDR3', NULL, 19, N'PCIe x16',  1, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Low Profile Compatible with Low Profile Bracket Included</li>
						<li>Chipset: Radeon HD5450 Engine Clock: 650 MHz Video Memory: 1GB DDR3</li>
						<li>Memory Clock: 1066MHz Memory Interface: 128-bit Bus: PCI-Express 2.0 x16</li>
						<li>RAMDAC: 400 MHz Max. Resolution: 2560x1600</li></ul>'),
		--26
		(3, N'Gigabyte Radeon RX 570 4 GB Gaming 4G', N'Gigabyte', N'Black', 409.99, 57, 12, 1, 
		NULL, N'Radeon RX 570', N'GDDR5', NULL, 150, N'PCIe x16',  4, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Core Clock (MHz): 1255 in OC Mode and 1244 in Gaming Mode.6+1 power phases on the card make the MOSFET working at a lower temperature and provide more stable voltage output</li>
						<li>WINDFORCE 2X with Blade Fan Design</li>
						<li>RGB fusion – 16.8M customizable color lighting</li>
						<li>Intuitive AORUS Graphics Engine</li></ul>')
GO
-- PRODUCT - RAM / MEMORY
INSERT INTO [Product] ([CateId], [Name], [Manufacturer], [Color], [Price], [Stock], [WarrantyPeriod], [Status], 
						[Socket], [Chipset], [MemoryType], [FormFactor], [TDP], [Interface], [Memory], 
						[Series], [Thread], [Core], [MemorySlot], [StorageType], [PSUWattage], [MemoryModules], 
						[PSUFormFactor], [ScreenSize], [Resolution], [Description])
VALUES	--27
		(4, N'Corsair Vengeance LPX 16 GB (2 x 8 GB) DDR4-3000', N'Corsair', N'Yellow', 90.98, 80, 12, 1, 
		NULL, NULL, N'DDR4', NULL, NULL, NULL, 16, 
		NULL, NULL, NULL, NULL, NULL, NULL, 2, 
		NULL, NULL, NULL, N'<ul><li>Designed for high performance overclocking</li>
						<li>Designed for great looks; SPD Speed: 2133MHz</li>
						<li>Compatibility: Intel 100 Series,Intel 200 Series,Intel 300 Series,Intel X299</li>
						<li>Low profile heat spreader design</li></ul>'),
		--28
		(4, N'Corsair Vengeance RGB Pro 32 GB (2 x 16 GB) DDR4-3200', N'Corsair', N'Black', 164.99, 50, 12, 2, 
		NULL, NULL, N'DDR4', NULL, NULL, NULL, 32, 
		NULL, NULL, NULL, NULL, NULL, NULL, 2, 
		NULL, NULL, NULL, N'<ul><li>Dynamic Multi Zone RGB Lighting</li>
						<li>Next Generation Software. Compatibility Intel 100 Series, Intel 200 Series, Intel 300 Series, Intel X299</li>
						<li>Custom Performance PCB. SPD Speed-2133MHz</li>
						<li>Maximum Bandwidth and Tight Response Times</li></ul>'),
		--29
		(4, N'G.Skill Trident Z RGB 16 GB (2 x 8 GB) DDR4-3200', N'G.Skill', N'Black', 96.99, 50, 12, 1, 
		NULL, NULL, N'DDR4', NULL, NULL, NULL, 16, 
		NULL, NULL, NULL, NULL, NULL, NULL, 2, 
		NULL, NULL, NULL, N'<ul><li>DDR4 3200 (PC4 25600)</li>
						<li>Timing 16-18-18-38</li>
						<li>Cas Latency 16</li>
						<li>Voltage 1.35V</li></ul>'),
		--30
		(4, N'Crucial 64 GB (1 x 64 GB) Registered DDR4-2400', N'Crucial', N'Black', 1207, 5, 12, 1, 
		NULL, NULL, N'DDR4', NULL, NULL, NULL, 64, 
		NULL, NULL, NULL, NULL, NULL, NULL, 1, 
		NULL, NULL, NULL, N'<ul><li>Crucial 64gb Ddr4-2400 Lrdimm - 64 Gb (1 X 64 Gb)</li>
						<li>Ddr4 Sdram</li>
						<li>2400 Mhz Ddr4-2400/pc4-19200</li>
						<li>1.20 V - Ecc - Registered</li></ul>'),
		--31
		(4, N'Crucial Ballistix Sport LT 16 GB (2 x 8 GB) DDR4-2666', N'Crucial', N'Red', 88.98, 30, 12, 1, 
		NULL, NULL, N'DDR4', NULL, NULL, NULL, 16, 
		NULL, NULL, NULL, NULL, NULL, NULL, 2, 
		NULL, NULL, NULL, N'<ul><li>Faster speeds and responsiveness than standard DDR4 memory</li>
						<li>Ideal for gamers and performance enthusiasts</li>
						<li>Intel XMP 2.0 profiles for easy configuration</li>
						<li>Digital camo heat spreader available in white, gray and red</li></ul>'),
		--32
		(4, N'Kingston HyperX Fury 16 GB (2 x 8 GB) DDR4-3200', N'Kingston', N'Black', 92.93, 80, 12, 1, 
		NULL, NULL, N'DDR4', NULL, NULL, NULL, 16, 
		NULL, NULL, NULL, NULL, NULL, NULL, 2, 
		NULL, NULL, NULL, N'<ul><li>Updated low-profile heat spreader design</li>
						<li>Intel XMP-ready profiles optimized for Intel’s latest chipsets</li>
						<li>Available in fast speeds up to 3466MHz/li>
						<li>Plug N Play functionality at 2400MHz and 2666MHz</li></ul>'),
		--33
		(4, N'Kingston HyperX Fury Blue 8 GB (2 x 4 GB) DDR3-1866', N'Kingston', N'Blue', 54.81, 0, 6, 3, 
		NULL, NULL, N'DDR3', NULL, NULL, NULL, 8, 
		NULL, NULL, NULL, NULL, NULL, NULL, 2, 
		NULL, NULL, NULL, N'<ul><li>Automatic Overclocking reach faster speeds and higher capacities by just installing the memory, no adjustments in BIOS needed</li>
						<li>Reliable 100 percent factory tested</li></ul>'),
		--34
		(4, N'G.Skill Ripjaws X Series 16 GB (2 x 8 GB) DDR3-2400', N'G.Skill', N'Blue', 162.69, 7, 6, 1, 
		NULL, NULL, N'DDR3', NULL, NULL, NULL, 16, 
		NULL, NULL, NULL, NULL, NULL, NULL, 2, 
		NULL, NULL, NULL, N'<ul><li>F3-2400C11D-16GXM</li></ul>')
GO
-- PRODUCT - PSU
INSERT INTO [Product] ([CateId], [Name], [Manufacturer], [Color], [Price], [Stock], [WarrantyPeriod], [Status], 
						[Socket], [Chipset], [MemoryType], [FormFactor], [TDP], [Interface], [Memory], 
						[Series], [Thread], [Core], [MemorySlot], [StorageType], [PSUWattage], [MemoryModules], 
						[PSUFormFactor], [ScreenSize], [Resolution], [Description])
VALUES	--35
		(5, N'Corsair RM (2019) 750 W 80+ Gold Certified Fully Modular ATX', N'Corsair', N'Black', 119.99, 50, 12, 1, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL,
		NULL, NULL, NULL, NULL, NULL, 750, NULL,
		'ATX', NULL, NULL, N'<ul><li>80 plus Gold certified: high efficiency operation for lower power consumption, less noise and cooler temperatures.</li>
						<li>Tuned for low noise operation: a 140mm rifle Bearing fan with a specially calculated fan curve ensures that fan noise is kept to minimum, even at full load.</li>
						<li>105°C-rated capacitors: industrial-grade capacitors deliver solid electrical performance and reliability.</li>
						<li>Zero RPM fan mode: at low and medium loads The cooling fan switches off entirely for near-silent operation.</li></ul>'),
		--36
		(5, N'EVGA BQ 600 W 80+ Bronze Certified Semi-modular ATX', N'EVGA', N'Black', 64.5, 70, 12, 1, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL,
		NULL, NULL, NULL, NULL, NULL, 600, NULL,
		'ATX', NULL, NULL, N'<ul><li>EVGA 600 BQ: Great Quality, Great Value</li>
						<li>80 PLUS Bronze certified, with 85 percent efficiency or higher under typical loads</li>
						<li>Fan Size / Bearing: 120 millimeters Fluid Dynamic Bearing Quiet and Intelligent Auto Fan for near silent operation</li>
						<li>AC Input 100 to 240 VAC, 10 5A, 50 to 60 hertz; Operating Temperature 0 degree to 40 degree Celsius</li></ul>'),
		--37
		(5, N'SeaSonic FOCUS Plus Gold 650 W 80+ Gold Certified Fully Modular ATX', N'SeaSonic', N'Black', 123.73, 37, 120, 1, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL,
		NULL, NULL, NULL, NULL, NULL, 650, NULL,
		'ATX', NULL, NULL, N'<ul><li>Full modular; use only the cables you need to reduce clutter and improve airflow for a better ventilated system</li>
						<li>80 Plus Gold; 87% efficient at 20% load, 90% efficient at 50% load, and 87% efficient at 100% load</li>
						<li>Hybrid silent fan control; The industry''s first, Seasonic patented, advanced three-phased thermal control achieves optimal between silence and cooling. This Hybrid Silent Fan Control functions in three operational stages: Fanless Mode, Silent Mode, and Cooling Mode</li>
						<li>10 year warranty; Our commitment to superior quality</li></ul>'),
		--38
		(5, N'Thermaltake Smart 600 W 80+ Certified ATX', N'Thermaltake', N'Black', 43.81, 100, 60, 1, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL,
		NULL, NULL, NULL, NULL, NULL, 600, NULL,
		'ATX', NULL, NULL, N'<ul><li>Delivers 600 watt Continuous output @ +40℃. Compliance with Intel ATX 12 Volt 2.31 & EPS 12V 2.92 standards</li>
						<li>80 PLUS Certified – 80percentage efficiency under typical load. Power good signal is 100 to 500 millisecond</li>
						<li>Supports (2) PCI E 6+2pin Connectors. Active (PFC) Power Factor Correction, MTBF: 100,000 hours</li>
						<li>Equipped with a powerful +12V rail, superior performance under all types of system loading</li></ul>'),
		--39
		(5, N'Corsair VS 450 W 80+ Certified ATX', N'Corsair', N'Black', 49.99, 0, 24, 3, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL,
		NULL, NULL, NULL, NULL, NULL, 450, NULL,
		'ATX', NULL, NULL, N'')
GO
-- PRODUCT - STORAGE
INSERT INTO [Product] ([CateId], [Name], [Manufacturer], [Color], [Price], [Stock], [WarrantyPeriod], [Status], 
						[Socket], [Chipset], [MemoryType], [FormFactor], [TDP], [Interface], [Memory], 
						[Series], [Thread], [Core], [MemorySlot], [StorageType], [PSUWattage], [MemoryModules], 
						[PSUFormFactor], [ScreenSize], [Resolution], [Description])
VALUES	--40
		(6, N'Western Digital Caviar Blue 1 TB 3.5" 7200RPM', N'Western Digital', N'Blue', 44.99, 200, 24, 1, 
		NULL, NULL, NULL, N'3.5"', NULL, N'SATA 6 Gb/s', 1000, 
		NULL, NULL, NULL, NULL, N'HDD', NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>WD quality and reliability</li>
						<li>Free Acronis True Image WD Edition cloning software</li>
						<li>Massive capacities up to 1 TB available</li>
						<li>2-year manufacturer''s limited warranty</li></ul>'),
		--41
		(6, N'Samsung 970 Evo 500 GB M.2-2280 NVME', N'Samsung', N'None', 87.99, 150, 60, 1, 
		NULL, NULL, NULL, N'M.2-2280', NULL, N'M.2 (M)', 500, 
		NULL, NULL, NULL, NULL, N'SSD', NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>INNOVATIVE V-NAND TECHNOLOGY: Powered by Samsung V-NAND Technology, the 970 EVO SSD’s NVMe interface (PCIe M.2 2280) offers enhanced bandwidth, low latency, and power efficiency ideal for tech enthusiasts, high end gamers, and 4K & 3D content designers</li>
						<li>BREAKTHROUGH READ WRITE SPEEDS: Sequential read and write performance levels of up to 3,500MB/s and 2,500MB/s, respectively; Random Read (4KB, QD32): Up to 500,000 IOPS Random Read</li>
						<li>SUPERIOR HEAT DISSIPATION: Samsung’s Dynamic Thermal Guard automatically monitors and maintains optimal operating temperatures to minimize performance drops</li>
						<li>5-YEAR LIMITED WARRANTY: 5-year limited warranty or 600 TBW (Terabytes Written)</li></ul>'),
		--42
		(6, N'Seagate Barracuda Compute 2 TB 3.5" 7200RPM', N'Seagate', N'Green', 49.99, 200, 24, 1, 
		NULL, NULL, NULL, N'M3.5"', NULL, N'SATA 6 Gb/s', 2000, 
		NULL, NULL, NULL, NULL, N'HDD', NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Versatile HDDs for all your PC needs bring you industry-leading excellence in personal computing</li>
						<li>Advanced Power modes help save energy without sacrificing performance</li>
						<li>SATA 6Gb/s interface optimizes burst performance; 256MB Cache</li>
						<li>7200 RPM</li></ul>'),
		--43
		(6, N'Crucial P1 1 TB M.2-2280 NVME', N'Crucial', N'None', 117.99, 80, 36, 1, 
		NULL, NULL, NULL, N'M.2-2280', NULL, N'M.2 (M)', 1000, 
		NULL, NULL, NULL, NULL, N'SSD', NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Capacities up to 1TB with sequential reads/writes up to 2,000/1,700 MB/s</li>
						<li>NVMe PCIe interface marks the next step in storage innovation.
						Micron 3D NAND advancing the World''s memory and storage technology for 40 years.</li>
						<li>NVMe Standard Self Monitoring and Reporting Technology (Smart)</li>
						<li>Redundant Array of Independent NAND (RAIN)</li></ul>'),
		--44
		(6, N'Seagate BarraCuda 4 TB 3.5" 5400RPM', N'Seagate', N'Green', 82.28, 0, 24, 3, 
		NULL, NULL, NULL, N'M3.5"', NULL, N'SATA 6 Gb/s', 4000, 
		NULL, NULL, NULL, NULL, N'HDD', NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>Build a powerhouse gaming computer or desktop setup with a variety of capacities and form factors</li>
						<li>The go to SATA hard drive solution for nearly every PC application from music to video to photo editing to PC gaming</li>
						<li>Confidently rely on internal hard drive technology backed by 20 years of innovation</li>
						<li>Enjoy long term peace of mind with the included two year limited warranty</li></ul>'),
		--45
		(6, N'TCSunBow X3 1 TB 2.5"', N'TCSunBow', N'None', 0, 0, 36, 2, 
		NULL, NULL, NULL, N'2.5"', NULL, N'SATA 6 Gb/s', 1000, 
		NULL, NULL, NULL, NULL, N'SSD', NULL, NULL, 
		NULL, NULL, NULL, N''),
		--46
		(6, N'Samsung 860 Evo 250 GB 2.5"', N'Samsung', N'None', 59.99, 100, 60, 1, 
		NULL, NULL, NULL, N'2.5"', NULL, N'SATA 6 Gb/s', 250, 
		NULL, NULL, NULL, NULL, N'SSD', NULL, NULL, 
		NULL, NULL, NULL, N'<ul><li>INNOVATIVE V NAND TECHNOLOGY: Powered by Samsung V NAND Technology, the 860 EVO SSD offers optimized performance for everyday computing as well as rendering large sized 4K videos and 3D data used by the latest applications</li>
						<li>ENHANCED READ WRITE SPEEDS: Sequential read and write performance levels of up to 550MB/s and 520MB/s, respectively</li>
						<li>SECURE ENCRYPTION: Protect data by selecting security options, including AES 256 bit hardware based encryption compliant with TCG Opal and IEEE 1668</li>
						<li>WARRANTY AND COMPATIBILITY: 5 year limited warranty; Windows 8/Windows 7/Windows Server 2003 (32 bit and 64 bit), Vista (SP1 and above), XP (SP2 and above), MAC OSX and Linux</li></ul>')
GO
-- PRODUCT - CPU COOLER
INSERT INTO [Product] ([CateId], [Name], [Manufacturer], [Color], [Price], [Stock], [WarrantyPeriod], [Status], 
						[Socket], [Description])  
VALUES	--47
		(7, 'Cooler Master Hyper 212 EVO 82.9 CFM Sleeve Bearing', 'Cooler Master', 'None', 29.99, 40, 12, 1,
		'AM2,AM2+,AM3,AM3+,FM1,FM2,FM2+,AM4,LGA775,LGA1150,LGA1151,LGA1155,LGA1156,LGA1366,LGA2011,LGA2011-3,LGA2066',
		'<ul><li>Air flow: 82.9 CFM; Noise level:36.0 decibels</li>
		<li>Fan Dimensions:120 x 120 x 25 millimeter (4.7 x 4.7 x 1 inches)</li>
		<li>Heat sink Dimensions: 116 x 51 x 159 millimeter (4.6 x 2 x 6.2 inch)</li>
		<li></li>
		<li></li></ul>'),
		--48
		(7, 'Corsair H100i RGB PLATINUM 75 CFM Liquid', 'Corsair', 'Black', 157.99, 25, 12, 1,
		'AM2,AM2+,AM3,AM3+,FM1,FM2,FM2+,AM4,LGA775,LGA1150,LGA1151,LGA1155,LGA1156,LGA1366,LGA2011,LGA2011-3,LGA2066,sTR4,sTRX4',
		'<ul><li>Two 120mm ML PRO Series RGB magnetic levitation PWM fans deliver a blast of color and improved airflow for extreme CPU cooling performance. Air flow - 75 CFM. Noise level - 37.0 decibels</li>
		<li>16 Individually controlled RGB LEDs light up the pump head to produce stunning, customizable lighting effects to match your build</li>
		<li>CORSAIR iCUE software allows you to control your cooler’s RGB lighting, monitor CPU and coolant temperatures, and adjust fan and pump speeds, all from a single intuitive interface</li>
		<li>Thermally optimized cold plate and low-noise pump design for high performance, quiet cooling</li>
		<li>Compatible Sockets: Intel LGA 115x, 1366, 2011, 2011-3, 2066 and AMD FM1, FM2, AM2, AM3, AM4, TR4</li></ul>'),
		--49
		(7, 'AMD Wraith Max 55.78 CFM', 'AMD', 'None', 37.20, 40, 12, 1,
		'AM2,AM2+,AM3,AM3+,FM1,FM2,FM2+,AM4',
		'<ul><li>UPC: 730143308779</li>
		<li>Weight: 1.350 lbs</li>
		<li></li>
		<li></li>
		<li></li></ul>'),
		--50
		(7, 'Noctua NH-D15 CHROMAX.BLACK 82.52 CFM', 'Noctua', 'None', 99.95, 30, 12, 1,
		'AM2,AM2+,AM3,AM3+,FM1,FM2,FM2+,AM4,LGA775,LGA1150,LGA1151,LGA1155,LGA1156,LGA1366,LGA2011,LGA2011-3,LGA2066',
		'<ul><li>Proven premium heatsink (more than 300 awards and recommendations from international hardware websites), now available in an all-black design that goes great with many colour schemes and RGB LEDs</li>
		<li>Extra-wide 140mm dual-tower design with 6 heatpipes and dual fans provides maximum quiet cooling efficiency on a par with many all-in-one watercoolers, ideal for overclockers and silent-enthusiasts!</li>
		<li>Dual-fan design with renowned, award-winning NF-A15 140mm fans with Low-Noise Adaptors and PWM for automatic speed control: Full cooling performance under load, whisper quiet at idle!</li>
		<li>Includes high-end NT-H1 thermal paste and SecuFirm2 mounting system for easy installation on Intel LGA1150, LGA1151, LGA1155, LGA1156, LGA2011, LGA2066 and AMD AM4, AM3(+), AM2(+), FM1, FM2(+)</li>
		<li>Renowned Noctua quality backed up by 6-year manufacturer’s warranty, deluxe choice for Intel Core i9, i7, i5, i3 (e.g. 9900K, 9700K, 8700K, 9980XE) and AMD Ryzen (e.g. 3900X, 3700X, 2700X, 2600X)</li></ul>')
GO
-- PRODUCT - CASE
INSERT INTO [Product] ([CateId], [Name], [Manufacturer], [Color], [Price], [Stock], [WarrantyPeriod], [Status], 
						[FormFactor], [PSUFormFactor], [Description])
VALUES	--51
		(8, N'NZXT H510 ATX Mid Tower', N'NZXT', N'White', 69.98, 40, 12, 1,
		N'ATX', N'ATX', 
		N'<ul><li>New features: Front I/O USB Type-C Port and tempered glass side panel with single screw installation</li>
		<li>Enhanced cable management: Our patented cable routing kit with pre-installed channels and straps makes wiring easy and intuitive</li>
		<li>Streamlined cooling: Two Aer F120 millimeter fans included for optimal internal airflow and the front panel and PSU intakes include removable filters, removeable bracket designed for radiators up to 240 millimeter - simplifies the installation of either closed-loop or custom-loop water cooling</li>
		<li>Motherboard support: Mini-ITX, MicroATX, and ATX</li>
		<li></li></ul>'),
		--52
		(8, N'Cooler Master MasterBox Q300L MicroATX Mini Tower', N'Cooler Master', N'Black', 44.99, 20, 12, 1,
		N'Micro ATX', N'ATX', 
		N'<ul><li>I/O panel can be adjusted in 6 different locations and the Case can be positioned: vertical or horizontal</li>
		<li>Edge to Edge Acrylic Transparent Side Panel offers a full view inside</li>
		<li>Body depth height: can support normal size ATX power supply</li>
		<li>Front Magnetic Design Dust Filter with open perforated front, top and bottom for high air performance</li>
		<li>Extra space behind the motherboard tray for hidden cable management</li></ul>'),
		--53
		(8, N'Corsair SPEC-DELTA RGB ATX Mid Tower', N'Corsair', N'Black', 79.99, 30, 12, 1,
		N'ATX', N'ATX', 
		N'<ul><li>The front panel’s dark, angular accents become transparent when backlit, providing a unique window into your system.</li>
		<li>A full-length PSU cover hides your PSU and cables out of sight, making creating immaculate builds easier than ever.</li>
		<li>Four included 120mm cooling fans (3x front, 1x rear) provide incredible airflow to cool your system, with room for up to a 360mm radiator in the front, 240mm radiator in the top and 120mm radiator in the rear.</li>
		<li>Connect the three RGB fans to Asus Aura Sync, Gigabyte RGB Fusion or MSI Mystic Light motherboards to customize and control RGB fan lighting.</li>
		<li>Two combo 3.5/2.5in trays in a removable cage, a 2.5in tray and a 2.5in mount provide a wealth of storage options.</li></ul>'),
		--54
		(8, N'Asus ROG Strix Helios ATX Full Tower', N'Asus', N'Black', 299.9, 0, 12, 1,
		N'ATX', N'ATX', 
		N'<ul><li>Mid-tower with support for EATX motherboards featuring a metal chassis, 4mm-thick smoked, tempered-glass full view side panels, three easy-to-clean dust filters, and space for 9 Storage drives</li>
		<li>Premium cable management includes translucent rear panel, adjustable multi-function cover featuring GPU support brackets and pre-run chassis cables</li>
		<li>Keep your cool with built in fan hub, four 140mm fans and up to seven optional fan-mounting points</li>
		<li>Custom Liquid Cooling ready features a water pump/ reservoir mounting bracket and support for radiators up to 420mm in length and 90mm thick (with fans)</li>
		<li>Graphic card mounting options include Up to 2x vertically mounted Graphics cards or 3x horizontally mounted Graphics cards using the 7 expansion Slots</li></ul>')
GO
-- PRODUCT - MONITOR
INSERT INTO [Product] ([CateId], [Name], [Manufacturer], [Color], [Price], [Stock], [WarrantyPeriod], [Status], 
					 [ScreenSize], [Resolution], [Description])
VALUES	--55
		(9, N'AOC C24G1 24.0" 1920x1080 144 Hz', N'AOC', N'Black', 144.99, 50, 36, 1, 
		 24, '1920x1080', 
		 N'<ul><li>AOC Gaming 24" Class, 23. 6" Viewable AOC Gaming monitor with 1920 x 1080 Full HD resolution</li>
		<li>F1500R curved monitor (VA panel) wrapping around your vision for an immersive gaming experience</li>
		<li>Rapid 1ms (MPRT) response and 144Hz refresh rate with AMD Free Sync for smoothest competitive game play</li>
		<li>Displayport, 2x HDMI 1. 4, VGA inputs with audio Line-out for convenience</li></ul>'),
		--56
		(9, N'Dell S2719DGF 27.0" 2560x1440 155 Hz', N'Dell', N'Silver', 299.99, 50, 24, 1, 
		 27, '2560x1440', 
		 N'<ul><li>Experience sharp, tear free Graphics with a Swift refresh rated to 155 Hertz (overclocked) and AMD free sync for Super smooth visuals</li>
		<li>Enjoy vivid edge to edge game play and crisp QHD resolution. You''ll get lost in the 3.68 million pixels almost two times more than full HD</li>
		<li>Brightness:350 candela per square metre</li>
		<li>Get blazing fast and responsive gameplay with minimum input lag at an extremely Rapid 1ms response time</li></ul>'),
		--57
		(9, N'LG 34GK950F-B 34.0" 3440x1440 144 Hz', N'LG', N'Black', 849.99, 20, 24, 1, 
		 34, '3440x1440', 
		 N'<ul><li>34 inches WQHD (3440 x 1440) Nano IPS Ultra Wide Display</li>
		<li>Radeon Free Sync 2 Technology with 144 Hertz</li>
		<li>4 Side Virtually Borderless Design</li>
		<li>Height/Swivel/Tilt Adjustable Stand</li></ul>'),
		--58
		(9, N'Alienware AW2518H 24.5" 1920x1080 240 Hz', N'Alienware', N'Black', 458.98, 25, 24, 1, 
		 24.5, '1920x1080', 
		 N'<ul><li>Designed for the enthusiast, the AW2518H delivers a futuristic style and precise form with solid stability; The contrast ratio is 1000:1 and it has 16.7 million colors of color support</li>
		<li>See gaming differently with NVIDIA G SYNC; This breakthrough display technology eliminates screen tearing and minimizes display stutter and input lag</li>
		<li>Lightning fast 240 Hertz native refresh rate combined with 1ms response time delivers buttery smooth game play with virtually no input lag</li>
		<li>Compatibility All Operating System; Specific gaming OSD (onscreen display) design keeps your user experience in the gaming theme</li></ul>'),
		--59
		(9, N'Acer K202HQL 19.5" 1600x900 60 Hz', N'Acer', N'Black', 64.99, 100, 24, 1, 
		 19.5, '1600x900', 
		 N'<ul><li>19.5-inch (1600 x 900) Widescreen TN Display</li>
		<li>Response time: 5ms. Brightness: 200 cd/m²</li>
		<li>Pixel Pitch: 0.27mm. Input Voltage - 110 V AC, 220 V AC</li>
		<li>Signal inputs: 1 x DVI & 1 x VGA; Horizontal Viewing Angle: 90°</li></ul>')
GO
-- PRODUCT - MORE
INSERT INTO [Product] ([CateId], [Name], [Manufacturer], [Color], [Price], [Stock], [WarrantyPeriod], [Status], 
						[Socket], [Chipset], [MemoryType], [FormFactor], [TDP], [Interface], [Memory], 
						[Series], [Thread], [Core], [MemorySlot], [StorageType], [PSUWattage], [MemoryModules], 
						[PSUFormFactor], [ScreenSize], [Resolution], [Description])  
VALUES	--60
		(2, N'ASRock H110M-HDS R3.0 Micro ATX LGA1151', N'ASRock', N'Orange', 51.99, 100, 12, 1, 
		N'LGA1151', N'Intel H110', N'DDR4', N'Micro ATX', NULL, NULL, NULL,
		NULL, NULL, NULL, 2, NULL, NULL, NULL, 
		NULL, NULL, NULL, 
		N'<ul><li>Memory: 2x DDR4-2133 DIMM Slots, Dual-Channel, ECC, Non-ECC, Unbuffered, Max capacity of 32GB</li>
		<li>Slots: 1x PCI-Express 3.0 x16 Slot, 1x PCI-Express 2.0 x1 Slot</li>
		<li>Supports 2 Way AMD Crossfire Technology</li>
		<li>SATA: 4x SATA3 Ports, Support NCQ, AHCI and Hot Plug</li>
		<li>LAN: Realtek RTL8111C PCI-Express x1 Gigabit Ethernet Controller</li></ul>'),
		--61
		(1, 'Intel Pentium G4400 3.3 GHz Dual-Core', 'Intel', 'None', 55.5, 100, 36, 1,
		'LGA1151', NULL, NULL, NULL, 54, NULL, NULL,
		'Intel Pentium', 2, 2, NULL, NULL, NULL, NULL,
		NULL, NULL, NULL, 
		'<ul><li>Made in China</li>
		<li>Instruction set is 64 bit. Instruction set extensions are intel sse4.1 and intel sse4.2</li></ul>'),
		--62
		(4, N'Team Elite 4 GB (1 x 4 GB) DDR4-2400', N'Team', N'Black', 44.99, 50, 12, 1, 
		NULL, NULL, N'DDR4', NULL, NULL, NULL, 4, 
		NULL, NULL, NULL, NULL, NULL, NULL, 1, 
		NULL, NULL, NULL, N''),
		--63
		(6, N'TCSunBow X3 120 GB 2.5"', N'TCSunBow', N'Black', 19.98, 150, 24, 1, 
		NULL, NULL, NULL, N'2.5"', NULL, N'SATA 6 Gb/s', 120, 
		NULL, NULL, NULL, NULL, N'SSD', NULL, NULL, 
		NULL, NULL, NULL, NULL),
		--64
		(5, N'Antec 350 W ATX', N'Antec', N'None', 29.74, 50, 12, 1, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL,
		NULL, NULL, NULL, NULL, NULL, 350, NULL,
		'ATX', NULL, NULL, 
		N'<ul><li>ATX12Vcompliant for compatibility with AMD and Intel systems</li>
		<li>Dimensions: L5.9" x W5.5" x H3.4"</li></ul>'),
		--65
		(8, N'Thermaltake Versa H22 ATX Mid Tower', N'Thermaltake', N'Black', 39.98, 100, 12, 1,
		NULL, NULL, NULL, N'ATX',NULL, NULL, NULL,
		NULL, NULL, NULL, NULL, NULL, NULL, NULL, 
		N'ATX', NULL, NULL,NULL),
		--66
		(9, N'Sceptre E225W-1920 22.0" 1920x1080 60 Hz', N'Sceptre', N'Black', 76.98, 50, 24, 1, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
		 22, '1920x1080', 
		 N'<ul><li>Build-in Speakers; Tilt: 15° backward and 5° forward</li>
		<li>VESA Wall Mount Ready</li>
		<li>Wide viewing in vertical and horizontal</li>
		<li>Fast 5ms response time</li></ul>'),
		--67
		(1, 'Intel Pentium Gold G5400 3.7 GHz Dual-Core', 'Intel', 'None', 62.3, 100, 36, 1,
		'LGA1151', NULL, NULL, NULL, 58, NULL, NULL,
		'Intel Pentium Gold', 2, 4, NULL, NULL, NULL, NULL,
		NULL, NULL, NULL, 
		'<ul><li>BX80684G5400</li>
		<li>INTEL</li></ul>'),
		--68
		(2, N'MSI H310M PRO-VDH PLUS Micro ATX LGA1151', N'MSI', N'Black', 55.99, 100, 12, 1, 
		N'LGA1151', N'Intel H310', N'DDR4', N'Micro ATX', NULL, NULL, NULL,
		NULL, NULL, NULL, 2, NULL, NULL, NULL, 
		NULL, NULL, NULL, 
		N'<ul><li>Supports 9th/ 8th Gen Intel Core/ Pentium gold/ Celeron Processors for LGA 1151 socket</li>
		<li>Supports DDR4 Memory up to 2666MHz</li>
		<li>Audio Boost reward your ears with studio grade sound quality</li>
		<li>Ez Debug LED easiest way to troubleshoot</li>
		<li>X-boost software that auto-detects and allows you to boost the performance of any storage or USB device</li></ul>'),
		--69
		(8, N'Tecware Nexus ATX Mid Tower', N'Tecware', N'White', 40.59, 80, 12, 1,
		NULL, NULL, NULL, N'ATX',NULL, NULL, NULL,
		NULL, NULL, NULL, NULL, NULL, NULL, NULL, 
		N'ATX', NULL, NULL,NULL),
		--70
		(5, N'Aerocool Plus 600 W ATX', N'Aerocool', N'None', 32.54, 50, 24, 1, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL,
		NULL, NULL, NULL, NULL, NULL, 600, NULL,
		'ATX', NULL, NULL, NULL),
		--71
		(1, 'Intel Core i3-9100F 3.6 GHz Quad-Core', 'Intel', 'None', 79.84, 100, 36, 1,
		'LGA1151', NULL, NULL, NULL, 65, NULL, NULL,
		'Intel Core i3', 4, 4, NULL, NULL, NULL, NULL,
		NULL, NULL, NULL, 
		''),
		--72
		(8, N'Cooler Master MasterBox MB511 RGB ATX Mid Tower', N'Cooler Master', N'Black', 88.26, 50, 12, 1,
		NULL, NULL, NULL, N'ATX',NULL, NULL, NULL,
		NULL, NULL, NULL, NULL, NULL, NULL, NULL, 
		N'ATX', NULL, NULL,NULL)
GO
-- PRODUCT - END

-- PRODUCT IMAGE
INSERT INTO [ProductImage] ([ProductId], [ImagePath], [MainImage])
VALUES	(1, N'product/1/0.jpg', 1), (1, N'product/1/1.jpg', 0),
		(2, N'product/2/0.jpg', 1), (2, N'product/2/1.jpg', 0),
		(3, N'product/3/0.jpg', 1), (3, N'product/3/1.jpg', 0),
		(4, N'product/4/0.jpg', 1), (4, N'product/4/1.jpg', 0), (4, N'product/4/2.jpg', 0),
		(5, N'product/5/0.jpg', 1), (5, N'product/5/1.jpg', 0),
		(6, N'product/6/0.jpg', 1), (6, N'product/6/1.jpg', 0),
		(7, N'product/7/0.jpg', 1), (7, N'product/7/1.jpg', 0),
		(8, N'product/8/0.jpg', 1), (8, N'product/8/1.jpg', 0),	
		(9, N'product/9/0.jpg', 1), (10, N'product/10/0.jpg', 1),
		(11, N'product/11/0.jpg', 1), (11, N'product/11/1.jpg', 0), (11, N'product/11/2.jpg', 0),
		(12, N'product/12/0.jpg', 1), (12, N'product/12/1.jpg', 0),	
		(13, N'product/13/0.jpg', 1), (13, N'product/13/1.jpg', 0), (13, N'product/13/2.jpg', 0),
		(14, N'product/14/0.jpg', 1), (14, N'product/14/1.jpg', 0), (14, N'product/14/2.jpg', 0),
		(15, N'product/15/0.jpg', 1), (15, N'product/15/1.jpg', 0), (15, N'product/15/2.jpg', 0), (15, N'product/15/3.jpg', 0),
		(16, N'product/16/0.jpg', 1), (16, N'product/16/1.jpg', 0), (16, N'product/16/2.jpg', 0),
		(17, N'product/17/0.jpg', 1), (17, N'product/17/1.jpg', 0), (17, N'product/17/2.jpg', 0), (17, N'product/17/3.jpg', 0),
		(18, N'product/18/0.jpg', 1), (18, N'product/18/1.jpg', 0), (18, N'product/18/2.jpg', 0),
		(19, N'product/19/0.jpg', 1), (19, N'product/19/1.jpg', 0),	
		(20, N'product/20/0.jpg', 1), (20, N'product/20/1.jpg', 0), (20, N'product/20/2.jpg', 0),
		(21, N'product/21/0.jpg', 1), (21, N'product/21/1.jpg', 0),	
		(22, N'product/22/0.jpg', 1), (22, N'product/22/1.jpg', 0),	
		(23, N'product/23/0.jpg', 1), (23, N'product/23/1.jpg', 0), (23, N'product/23/2.jpg', 0), (23, N'product/23/3.jpg', 0),
		(24, N'product/24/0.jpg', 1),
		(25, N'product/25/0.jpg', 1),
		(26, N'product/26/0.jpg', 1), (26, N'product/26/1.jpg', 0), (26, N'product/26/2.jpg', 0),
		(27, N'product/27/0.jpg', 1), 
		(28, N'product/28/0.jpg', 1),
		(29, N'product/29/0.jpg', 1), (29, N'product/29/1.jpg', 0), (29, N'product/29/2.jpg', 0),
		(30, N'product/30/0.jpg', 1),
		(31, N'product/31/0.jpg', 1), (31, N'product/31/1.jpg', 0), (31, N'product/31/2.jpg', 0),
		(32, N'product/32/0.jpg', 1), (32, N'product/32/1.jpg', 0),
		(33, N'product/33/0.jpg', 1),
		(34, N'product/34/0.jpg', 1), (34, N'product/34/1.jpg', 0),
		(35, N'product/35/0.jpg', 1), (35, N'product/35/1.jpg', 0), (35, N'product/35/2.jpg', 0), (35, N'product/35/3.jpg', 0), (35, N'product/35/4.jpg', 0),
		(36, N'product/36/0.jpg', 1), (36, N'product/36/1.jpg', 0), (36, N'product/36/2.jpg', 0),
		(37, N'product/37/0.jpg', 1), (37, N'product/37/1.jpg', 0),
		(38, N'product/38/0.jpg', 1), (38, N'product/38/1.jpg', 0),
		(39, N'product/39/0.jpg', 1),
		(40, N'product/40/0.jpg', 1),
		(41, N'product/41/0.jpg', 1), (41, N'product/41/1.jpg', 0), (41, N'product/41/2.jpg', 0), (41, N'product/41/3.jpg', 0), (41, N'product/41/4.jpg', 0),
		(42, N'product/42/0.jpg', 1), (42, N'product/42/1.jpg', 0),
		(43, N'product/43/0.jpg', 1), (43, N'product/43/1.jpg', 0),
		(44, N'product/44/0.jpg', 1), 
		(45, N'product/45/0.jpg', 1), 
		(46, N'product/46/0.jpg', 1), (46, N'product/46/1.jpg', 0), (46, N'product/46/2.jpg', 0),
		(47, N'product/47/0.jpg', 1), (47, N'product/47/1.jpg', 0), (47, N'product/47/2.jpg', 0), (47, N'product/47/3.jpg', 0),
		(48, N'product/48/0.jpg', 1), (48, N'product/48/1.jpg', 0), (48, N'product/48/2.jpg', 0), (48, N'product/48/3.jpg', 0), (48, N'product/48/4.jpg', 0),
		(49, N'product/49/0.jpg', 1), 
		(50, N'product/50/0.jpg', 1), (50, N'product/50/1.jpg', 0), (50, N'product/50/2.jpg', 0),
		(51, N'product/51/0.jpg', 1), (51, N'product/51/1.jpg', 0), (51, N'product/51/2.jpg', 0), (51, N'product/51/3.jpg', 0),
		(52, N'product/52/0.jpg', 1), (52, N'product/52/1.jpg', 0), (52, N'product/52/2.jpg', 0),
		(53, N'product/53/0.jpg', 1), (53, N'product/53/1.jpg', 0), (53, N'product/53/2.jpg', 0),
		(55, N'product/54/0.jpg', 1), (55, N'product/54/1.jpg', 0),
		(55, N'product/55/0.jpg', 1), (55, N'product/55/1.jpg', 0),
		(56, N'product/56/0.jpg', 1), (56, N'product/56/1.jpg', 0), (56, N'product/56/2.jpg', 0), (56, N'product/56/3.jpg', 0),
		(57, N'product/57/0.jpg', 1), (57, N'product/57/1.jpg', 0), (57, N'product/57/2.jpg', 0), (57, N'product/57/3.jpg', 0), (57, N'product/57/4.jpg', 0),
		(58, N'product/58/0.jpg', 1), (58, N'product/58/1.jpg', 0), (58, N'product/58/2.jpg', 0), (58, N'product/58/3.jpg', 0),
		(59, N'product/59/0.jpg', 1), (59, N'product/59/1.jpg', 0), (59, N'product/59/2.jpg', 0), (59, N'product/59/3.jpg', 0),
		(60, N'product/60/0.jpg', 1),
		(61, N'product/61/0.jpg', 1),
		(62, N'product/62/0.jpg', 1),
		(63, N'product/63/0.jpg', 1),
		(65, N'product/65/0.jpg', 1),
		(66, N'product/66/0.jpg', 1), (66, N'product/66/1.jpg', 0), (66, N'product/66/2.jpg', 0),
		(67, N'product/67/0.jpg', 1),
		(68, N'product/68/0.jpg', 1), (68, N'product/68/1.jpg', 0), (68, N'product/68/2.jpg', 0),
		(69, N'product/69/0.jpg', 1), (69, N'product/69/1.jpg', 0),
		(70, N'product/70/0.jpg', 1),
		(71, N'product/71/0.jpg', 1),
		(72, N'product/72/0.jpg', 1), (72, N'product/72/1.jpg', 0)
GO
-- PRODUCT IMAGE - END

-- PRODUCT COMMENT
INSERT INTO [ProductComment] ([ProductId], [CustomerId], [Content], [CreatedAt]) 
VALUES	--1
		(39, 5, N'I got this yesterday. But this morning, my PC didn''t boot up! What can I do?', CAST(N'2019-06-01T09:01:00.000' AS DateTime)),
		--2
		(27, 9, N'Is this compatible with ASRock B450M PRO4?', CAST(N'2019-08-22T09:01:00.000' AS DateTime)),
		--3
		(54, 1, N'What a badass case!! So cool!', CAST(N'2019-09-11T09:01:00.000' AS DateTime)),
		--4
		(24, 5, N'When will this available again?', CAST(N'2019-11-12T19:01:00.000' AS DateTime)),
		--5
		(46, 7, N'Best storage ever!!', CAST(N'2020-01-12T15:01:00.000' AS DateTime)),
		--6
		(46, 10, N'Damn, wish this have 64 GB version.', CAST(N'2020-01-24T09:01:00.000' AS DateTime)),
		--7
		(21, 3, N'Still laggy on PUBG', CAST(N'2020-02-02T09:01:00.000' AS DateTime))
GO
-- PRODUCT COMMENT - END

-- PRODUCT COMMENT REPLY
INSERT INTO [ProductCommentReply] ([ProductCommentId], [CustomerId], [StaffUsername], [Content], [CreatedAt]) 
VALUES	(1, NULL, N'viethai', N'Hi, please bring it to our shop for warranty.', CAST(N'2019-06-01T09:10:00.000' AS DateTime)),
		(1, 5, NULL, N'Thanks, got it fixed today.', CAST(N'2019-06-04T12:10:00.000' AS DateTime)),
		(2, NULL, 'customercare', N'Hi Thuy, they are compatible.', CAST(N'2019-08-22T10:10:00.000' AS DateTime)),
		(4, NULL, 'customercare', N'Hi Jordan, truely sorry that this product will not be imported again.', CAST(N'2019-11-13T08:11:00.000' AS DateTime))
GO
-- PRODUCT COMMENT REPLY - END

-- PREBUILT
INSERT INTO [PreBuilt] ([Name], [Detail], 
	[CaseId], [MonitorId], [PSUId], [StorageId], [VGAId], [MemoryId], [CPUCoolerId], [MotherBoardId], [CPUId], 
	[CustomerId], [StaffUsername], [CreatedAt]) 
VALUES	--1
		(N'HTD OFFICE PC 1', N'Normal pre-built pc for office user. (with 19.5" monitor)', 
		65, 59, 64, 63, NULL, 62, NULL, 60, 61, 
		NULL, N'haithien', CAST(N'2019-03-03T10:00:00.000' AS DateTime)),
		--2
		(N'HTD OFFICE PC 2', N'Better pre-built pc for office user. (with 22" monitor)', 
		65, 66, 64, 63, NULL, 62, NULL, 68, 67, 
		NULL, N'haithien', CAST(N'2019-03-03T10:15:00.000' AS DateTime)),
		--3
		(N'HTD GAMING PC 1', N'Budget Gaming PC for gamer. (no monitor)', 
		69, NULL, 70, 63, 21, 32, NULL, 68, 71, 
		NULL, N'ducdung', CAST(N'2019-03-03T10:20:00.000' AS DateTime)),
		--4
		(N'HTD GAMING PC 2', N'Mid-end Gaming PC for gamer. (no monitor)', 
		72, NULL, 36, 63, 20, 32, NULL, 16, 71, 
		NULL, N'ducdung', CAST(N'2019-03-03T10:20:00.000' AS DateTime))
GO
-- PREBUILT - END

-- PREBUILT IMAGE
INSERT INTO [PreBuiltImage] ([PreBuiltId], [Path])
VALUES (1, 'prebuilt/1/0.jpg'), (2, 'prebuilt/2/0.jpg'), (3, 'prebuilt/3/0.jpg'), (4, 'prebuilt/4/0.jpg')
GO
-- PREBUILT IMAGE - END

-- PREBUILT RATING
INSERT INTO [PreBuiltRating] ([PreBuiltId], [CustomerId], [Comment], [Rating], [CreatedAt])
VALUES	(1, 5, 'Can''t even play Pikachu!!', 4, CAST(N'2019-03-10T17:50:00.000' AS DateTime)),
		(1, 8, 'My dad is really like this! Even this build is pretty cheap!', 9, CAST(N'2019-04-21T15:50:00.000' AS DateTime)),
		(3, 10, 'Yeah this is real budget PC! Wish this could handle The Witcher 3 anyway.', 7, CAST(N'2019-07-07T18:20:00.000' AS DateTime)),
		(4, 3, 'Good build.', 10, CAST(N'2019-09-12T18:20:00.000' AS DateTime))
GO
-- PREBUILT RATING - END

-- PROMOTION DETAIL
INSERT [PromotionDetail] ([Name], [Detail], [Image], [Target], 
					[StartDate], [EndDate], [IsAlways], [IsDisabled]) 
VALUES	--1
		(N'Woman''s Day', N'Woman''s Day, 10% discount all motherboard.', N'promotion/1.png', 0, 
		CAST(N'2019-03-02T00:00:00.000' AS DateTime), CAST(N'2019-03-08T00:00:00.000' AS DateTime), 0, 1),
		--2
		(N'Valentine Pre-built PC', N'Sale off $10 any pre-built PC. Buy your girlfriend a good PC for now.', N'promotion/2.png', 0, 
		CAST(N'2020-02-07T00:00:00.000' AS DateTime), CAST(N'2020-02-14T00:00:00.000' AS DateTime), 0, 0),
		--3
		(N'HTD Pre-built PC (Member)', N'Permanently discount 5% any HTD exclusive pre-built PC for members.', N'promotion/3.png', 1, 
		NULL, NULL, 1, 0),
		--4
		(N'Gaming Newbie', N'Discount 5% on Intel Core i3-9100F and XFX Radeon RX 580 8 GB.', N'promotion/4.jpg', 0, 
		CAST(N'2020-02-01T00:00:00.000' AS DateTime), CAST(N'2020-03-01T00:00:00.000' AS DateTime), 0, 0)
GO
-- PROMOTION DETAIL - END

-- PROMOTION
INSERT [Promotion] ([PromotionDetailId], [CategoryId], [ProductId], [PreBuiltTarget], 
				[LimitedQuantity], [MinQuantity], [MaxQuantity], [QuantityLeft], 
				[Percentage], [ExactSaleOff], [MaxSaleOff]) 
VALUES	(1, 2, NULL, NULL, 
		50, NULL, NULL, 0, 
		10, NULL, 45),
		(2, NULL, NULL, 0, 
		50, NULL, NULL, 47, 
		NULL, 10, NULL),
		(3, NULL, NULL, 1, 
		NULL, NULL, NULL, NULL, 
		5, NULL, NULL),
		(4, NULL, 21, NULL, 
		40, NULL, NULL, 38, 
		5, NULL, NULL),
		(4, NULL, 71, NULL, 
		30, NULL, NULL, 18, 
		5, NULL, NULL)
GO
-- PROMOTION - END

-- IMAGE SLIDE
INSERT INTO ImageSlide ([Image], [Order], [Title], [Description], [Link], [Status])
VALUES	('imageslide/1.png', NULL, 'Test 1', 'Test 1', 'test/1', 1),
		('imageslide/2.png', NULL, 'Woman''s Day', 'Woman''s Day, 10% discount all motherboard.', 'promotion/1', 0),
		('imageslide/3.png', NULL, 'Samsung SSD Day', 'Samsung SSD Day', 'search?category=6&keyword=&page=1&manu=Samsung', 1),
		('imageslide/4.png', 1, 'Valentine Pre-built PC', 'Sale off $10 any pre-built PC. Buy your girlfriend a good PC for now.', 'promotion/2', 1),
		('imageslide/5.png', NULL, 'Tet Holiday Sale', 'Tet Holiday Sale', 'promotion/3', 0),
		('imageslide/6.png', 2, 'HTD Pre-built PC (Member)', 'Permanently discount 5% any HTD exclusive pre-built PC for members.', 'promotion/3', 1),
		('imageslide/7.jpg', 3, 'Gaming Newbie', 'Discount 5% on Intel Core i3-9100F and XFX Radeon RX 580 8 GB.', 'promotion/3', 1)
GO
-- IMAGE SLIDE - END

-- ORDER
-- ORDER - END

-- ORDER DETAIL
-- ORDER DETAIL - END

-- DELIVERY
-- DELIVERY - END