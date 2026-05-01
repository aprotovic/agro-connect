# Agro-Connect

A full-stack web platform that connects farmers directly with merchants for agricultural product trading. Built with Java, MySQL, and a responsive frontend.

## Features

**For Farmers**
- Account registration and authentication
- Product listing with inventory management (name, category, quantity, price)
- Order tracking and fulfillment dashboard
- Analytics overview of sales and active listings

**For Merchants**
- Product search and filtering across the marketplace
- Order placement with real-time availability checks
- Order history and status tracking
- Dashboard with spending and purchase analytics

**Admin Panel**
- Platform-wide user and order management
- System health monitoring

## Tech Stack

| Layer       | Technology                          |
|-------------|-------------------------------------|
| Frontend    | HTML5, CSS3, Vanilla JavaScript     |
| Backend     | Java (HttpServer, multi-threaded)   |
| Database    | MySQL 8.0+                          |
| Auth        | BCrypt password hashing             |
| Connectivity| JDBC with connection pooling        |

## Getting Started

### Prerequisites
- JDK 8+
- MySQL 8.0+ (standalone or via XAMPP)
- Git

### 1. Clone the Repository
```bash
git clone https://github.com/aprotovic/agro-connect.git
cd agro-connect
```

### 2. Database Setup
Create the database and import the schema:
```sql
CREATE DATABASE agro_connect CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```
Then import the SQL files:
```bash
mysql -u root -p agro_connect < database/schema.sql
mysql -u root -p agro_connect < database/sample_data.sql
```

### 3. Configure Connection
Edit `src/config/DatabaseConfig.java` if your MySQL credentials differ from the defaults:
```java
private static final String URL = "jdbc:mysql://localhost:3306/agro_connect";
private static final String USER = "root";
private static final String PASSWORD = "";
```

### 4. Build & Run

**Windows:**
```bash
build.bat
run.bat
```

**Linux/macOS:**
```bash
chmod +x build.sh run.sh
./build.sh
./run.sh
```

The server starts at **http://localhost:8080**.

## Project Structure
```
agro-connect/
├── src/
│   ├── AgroConnectServer.java      # HTTP server and route handler
│   ├── config/
│   │   └── DatabaseConfig.java     # Connection pool manager
│   ├── models/                     # Data models (Farmer, Merchant, Product, Order)
│   ├── dao/                        # Data access layer
│   └── utils/                      # JSON response utilities
├── webapp/
│   ├── index.html                  # Landing page
│   ├── farmer/                     # Farmer portal pages
│   ├── merchant/                   # Merchant portal pages
│   ├── admin/                      # Admin dashboard
│   ├── css/styles.css
│   └── js/main.js
├── database/
│   ├── schema.sql                  # Table definitions
│   └── sample_data.sql             # Seed data
└── lib/                            # JDBC driver and dependencies
```

## API Reference

### Authentication
| Method | Endpoint                    | Description            |
|--------|-----------------------------|------------------------|
| POST   | `/api/farmer/register`      | Register a farmer      |
| POST   | `/api/farmer/login`         | Farmer login           |
| POST   | `/api/merchant/register`    | Register a merchant    |
| POST   | `/api/merchant/login`       | Merchant login         |

### Products
| Method | Endpoint                    | Description            |
|--------|-----------------------------|------------------------|
| POST   | `/api/farmer/product/add`   | Add a product          |
| PUT    | `/api/farmer/product/update`| Update product details |
| GET    | `/api/products`             | List all products      |
| GET    | `/api/products/search?query=` | Search products      |

### Orders
| Method | Endpoint                    | Description            |
|--------|-----------------------------|------------------------|
| POST   | `/api/order/place`          | Place an order         |
| GET    | `/api/farmer/{id}/orders`   | Farmer's orders        |
| GET    | `/api/merchant/{id}/orders` | Merchant's orders      |

## Security

- Passwords hashed with **BCrypt** (salted)
- All database queries use **PreparedStatements** to prevent SQL injection
- **Pessimistic locking** (`SELECT ... FOR UPDATE`) on product rows during order transactions to prevent overselling under concurrency
- Input sanitization on both client and server side

## Roadmap
- [ ] Payment gateway integration (Stripe / Chapa)
- [ ] Product image uploads
- [ ] Real-time notifications (WebSocket)
- [ ] Ratings and reviews
- [ ] Mobile app (Android)
- [ ] Multi-language support (Amharic, Oromiffa)

## Contributing
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes
4. Open a pull request

## License
MIT

## Author
Built by [aprotovic](https://github.com/aprotovic)
