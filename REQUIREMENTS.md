# .msg Recycling Rewards Web App - CLEAN 

## 1. User Authentication

### 1.1 User Registration

#### Objective:
Allow new users to register securely with the application.

**Acceptance Criteria:**
- New users can successfully register by entering their email, password, first name, last name, and address.
- Email field undergoes proper validation with error messages for invalid entries.
- Upon successful registration, users are redirected to the login page.
- [Optional] Social Media integration: Users can choose to register using social media (Facebook, Google, etc.).

### 1.2 User Log In/ User Log Out

#### Objective:
Enable registered users to securely log in and access their personalized data.

**Acceptance Criteria:**
- Registered users, when logged out, can access their account by entering email and password on the login page.
- Incorrect login attempts result in appropriate error messages.
- Home Page displays the app logo, user name label, and a logout button.

## 2. Recycling Center List – Entry Point/Home Page

### 2.1 Recycling Center List

#### Objective:
Provide users with a comprehensive list of recycling centers based on selected filters.

**Acceptance Criteria:**
- After logging in, a dedicated "Recycling Centers" section presents a table view of all recycling centers.
- Table columns include Name, Location, Material Types Accepted, and Operating Hours.
- Dropdown menu allows users to filter centers based on accepted materials.
- Real-time updates of the table based on applied material filters.
- Search bar facilitates filtering based on center names, addresses, or relevant data.
- Partial matching supported in the search.
- "Reset" button clears all filters.
- Sorting functionality available for various criteria.
- Display a message if no recycling centers are available.

## 3. Adding a New Recycling Center

### 3.1 Adding a New Recycling Center

#### Objective:
Enable administrators to add new recycling centers with detailed information.

**Acceptance Criteria:**
- Administrators can input location details, materials-to-recycle, name, and working hours.
- Dropdowns for county, city, and materials-to-recycle offer quick-search functionality.
- Proper validations ensure data integrity.
- Validation message prompts for mandatory empty fields.
- Confirmation message appears upon successfully saving a new recycling center.
- The list of recycling centers updates after each save.

## 4. Recycling Tracking

### 4.1 Recycling Tracking

#### Objective:
Allow users to input recycled materials and view real-time statistics.

**Acceptance Criteria:**
- Input form for recycled material type and amount.
- Display of the sum of contributed materials per type and their environmental impact.
- Material type and amount undergo validation.
- Real-time updates of statistics after contributing materials.

## 5. Tracking Reward Points

### 5.1 Tracking Reward Points Based on Recycled Products

#### Objective:
Automatically calculate and display user reward points based on recycled products.

**Acceptance Criteria:**
- Automatic point calculation upon login.
- Recognition and assignment of points for different recycled product types.
- Real-time updates of total reward points.
- Notifications for earned points after each recycling activity.
- Tracking of total points and notification upon reaching the voucher threshold.
- Clear user documentation on point calculation and product types.

### 5.2 Recycling Rewards System - Voucher Download

#### Objective:
Allow users to download a PDF voucher containing their reward points for use at partner businesses.

**Acceptance Criteria:**
- Clear and easily accessible option to "Download Voucher" in the user dashboard or navigation menu.
- PDF voucher generation with user details, total reward points, expiration date, and a unique voucher code.
- Redemption instructions included in the voucher.
- Downloadable in PDF format.
- [Optional] Storage of voucher history in the account's transaction history.

## 6. Accessing Educational Resources

### 6.1 Accessing Educational Resources on Recycling and Sustainable Living

#### Objective:
Provide users with a dedicated section offering educational resources on recycling, environmental benefits, and sustainable living.

**Acceptance Criteria:**
- Clearly visible and easily accessible "Educational Resources" section.
- Diverse content types, including articles, videos, infographics, and tips.
- [Optional] Search and filter options for specific topics or categories.
- [Optional] User engagement features such as liking, sharing, or saving content.

## 7. Notification and Reminders

### 7.1 Notification and Reminders

#### Objective:
Implement notifications and reminders to encourage regular recycling among users. Reminders about recycling collection days or nearby events.

## 8. Gamification

### 8.1 Gamification

#### Objective:
Enhance user engagement by incorporating gamification elements such as challenges, badges, and leaderboards, fostering friendly competition among users.
