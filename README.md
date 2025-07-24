# Gateway: A Unified Immigration Process App (USCIS & ICE Integration)

## Overview and Vision

Gateway is a comprehensive Android app designed to **streamline the
entire U.S. immigration process** -- from a foreign national's initial
visa application in their home country all the way through becoming a
naturalized U.S. citizen. The goal is to **integrate all publicly
available government APIs and online services** into a single,
user-friendly interface, providing guidance and real-time updates at
each step. By prioritizing official government data (USCIS, ICE,
Department of State) and secure online connectivity, Gateway aims to
**accelerate legal immigration** while increasing transparency and
efficiency. This aligns with USCIS's own digital modernization goals --
the USCIS Torch API platform is intended to *"shorten decision
timelines, increase transparency, and more efficiently handle
immigration benefits
requests"*[\[1\]](https://constacloud.com/uscis-gov-api-integration-services.html#:~:text=will%20shorten%20decision%20timelines%2C%20increase,efficiently%20handle%20immigration%20benefits%20requests).
Gateway will serve as a meta-guide and task orchestrator for an agent
(or developer) implementing this plan, and ultimately form the basis of
a pitch to USCIS for collaboration once the MVP is ready.

## Quickstart

This repository includes a small Kotlin-based CLI.
Build the project and use the generated script:
=======
This repository includes a small Kotlin-based CLI. Build the project and
use the generated script:

```bash
gradle installDist
./gateway-android/build/install/gateway-android/bin/gateway-android case-status YOUR_RECEIPT_NUMBER
```

To print USCIS study materials for the naturalization test:

```bash
./gateway-android/build/install/gateway-android/bin/gateway-android study-resources
```


## Integration with Government APIs and Services

**1. USCIS Case Status API:** Gateway will leverage the official **USCIS
Case Status API** to provide up-to-date case information to users. This
RESTful API (offered via the USCIS Torch Platform) returns the latest
status of a user's application or petition given the USCIS receipt
number[\[2\]](https://developer.uscis.gov/apis#:~:text=Case%20Status%20API).
For example, after a user files a petition (I-130, I-140, etc.) or an
application (I-485, N-400, etc.), they can input their receipt number
and the app will regularly fetch the status ("Case Received",
"Fingerprint Scheduled", "Approved", etc.) directly from USCIS. The Case
Status API is intended for customers or their authorized representatives
who need frequent access to status
updates[\[3\]](https://developer.uscis.gov/apis#:~:text=Provides%20case%20status%20information%20to,access%20to%20case%20status%20information).
By integrating this API, the app ensures users no longer need to
manually check the USCIS website; they can receive **real-time push
notifications** whenever their case status changes. (Under the hood,
Gateway's agent will call the Case Status endpoint at appropriate
intervals, using the user's receipt number and an authorization token --
details on authentication are in the Security section below.)

**2. USCIS FOIA Request & Status API:** For deeper transparency, Gateway
will incorporate the **FOIA Request and Status API** provided by USCIS.
This allows the app to programmatically submit Freedom of Information
Act (FOIA) requests for an immigrant's **Alien File (A-File)** and then
track the request
status[\[4\]](https://developer.uscis.gov/apis#:~:text=FOIA%20Request%20and%20Status%20API).
For instance, an immigrant or their attorney might use FOIA to obtain
copies of prior immigration filings or records. Through Gateway, a user
can fill out the necessary details (or reuse profile data) to create a
FOIA request via API, and the app will return the assigned FOIA case
number and status. The FOIA API thus adds value for advanced users who
want to retrieve their historical immigration records; the app can
periodically check the FOIA case status by its request number and notify
the user when USCIS has processed the
request[\[4\]](https://developer.uscis.gov/apis#:~:text=FOIA%20Request%20and%20Status%20API).
This integration is fully online and secure, eliminating the need for
mailing forms or checking status via email. It demonstrates Gateway's
commitment to leveraging **all available government endpoints** to
streamline processes that were traditionally paper-based.

**3. USCIS Processing Times and Other USCIS Web Services:** In addition
to official developer APIs, Gateway will make use of public USCIS web
data for informational features. One key service is the **USCIS
Processing Times** data. USCIS provides an online tool for checking
typical processing durations for various forms at specific field
offices/service centers. Gateway can tap into the same **Processing
Times API** used by the USCIS website to fetch up-to-date processing
time ranges for the user's pending
applications[\[5\]](https://rd.thecoatlessprofessor.com/uscis-processing/#:~:text=Information%20is%20obtained%20by%20making,Processing%20Time%20API%20found%20at).
By calling the USCIS Processing Time API (e.g. at
`egov.uscis.gov/processing-times/api/`[\[6\]](https://rd.thecoatlessprofessor.com/uscis-processing/#:~:text=processing%20time%20form%20makes%20to,Processing%20Time%20API%20found%20at)),
the app can inform users how long cases like theirs *usually* take and
when they might be eligible for an inquiry about delays. This API can be
accessed by mimicking the official site's requests (as some researchers

have done using unofficial scripts)[\[5\]](https://rd.thecoatlessprofessor.com/uscis-processing/#:~:text=Information%20is%20obtained%20by%20making,Processing%20Time%20API%20found%20at).
=======
have done using custom scripts)[\[5\]](https://rd.thecoatlessprofessor.com/uscis-processing/#:~:text=Information%20is%20obtained%20by%20making,Processing%20Time%20API%20found%20at).
Gateway will integrate this carefully, ensuring compliance with any
usage policies (rate limiting the requests and caching results as
needed) while providing users a **dashboard of their case progress vs.
typical timelines**.

-   *Other USCIS tools:* Gateway will also include links or integrations
    to other USCIS online services. For example, the app can embed the
    **USCIS Field Office Locator** (to help users find their local USCIS
    office or Application Support Center for biometrics). It can link to
    online filing where available (many forms can now be filed via the
    USCIS online account system). While USCIS has not opened an API for
    submitting benefit forms (aside from FOIA) as of now, Gateway will
    guide users to the official **myUSCIS** online filing portals when
    possible (for instance, if a user is ready to file Form N-400 for
    naturalization, the app can deep-link into USCIS's web application).
    All such interactions will occur over HTTPS within the app's secure
    web views or by redirecting to the official site, ensuring users
    always transmit information through **official secure channels**.
    Government resources are prioritized -- for general information like
    form instructions, eligibility criteria, and policy updates, the app
    will pull directly from **USCIS.gov** and **USA.gov** content,
    rather than third-party sources. This guarantees that the guidance
    is authoritative and up-to-date.

**4. Department of State & NVC Integration:** The immigration journey
often involves the U.S. Department of State, especially for those coming
from abroad via consular processing. Gateway will integrate with
**public State Department tools** to cover this phase:

-   **NVC Case Status (Consular Electronic Application Center - CEAC):**
    After USCIS approves an immigrant petition, the case is handed off
    to the National Visa Center (State Dept.) for visa
    processing[\[7\]](https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition/step-2-begin-nvc-processing.html#:~:text=After%20USCIS%20approves%20your%20petition%2C,messages%2C%20and%20manage%20your%20case).
    NVC assigns a case number and provides an online portal (CEAC) where
    applicants can submit visa applications (e.g. DS-260), pay fees,
    upload civil documents, and **track their visa case
    status**[\[7\]](https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition/step-2-begin-nvc-processing.html#:~:text=After%20USCIS%20approves%20your%20petition%2C,messages%2C%20and%20manage%20your%20case).
    Gateway will guide users through this handoff: once a USCIS petition
    is approved (detected via the Case Status API), the app will prompt
    the user to transition to the NVC stage. The app can link to the
    CEAC login page for the user to enter their credentials (case ID and
    invoice number) -- since the CEAC portal is behind login and doesn't
    offer an open API, a full API integration isn't possible. However,
    Gateway can still simplify this stage by embedding a **CEAC status
    check** widget or automating reminders. For example, CEAC has a
    public **Visa Status Check** for both immigrant and nonimmigrant
    visas (which requires the applicant's case number and
    location)[\[8\]](https://ceac.state.gov/CEACStatTracker/Status.aspx?App=NIV#:~:text=CEAC%20Visa%20Status%20Check%20,Application%20ID%20or%20Case%20Number).
    Gateway can provide a form for the user to input those details and
    then fetch their visa application status from CEAC (by making an
    HTTPS request to the same status check endpoint used by the
    website). This would allow users to see if their interview is
    scheduled, if the visa is issued, etc., without manually visiting
    the site. All communications will be over a secure connection and no
    credentials will be stored, respecting the State Department's site
    usage policies.

-   **Embassy/Consulate Information:** The app will also integrate
    information about U.S. embassies/consulates for the interview stage.
    Using the **usembassy.gov** directory, Gateway can help users find
    the U.S. Embassy in their country and even directly navigate to the
    consulate's visa appointment scheduling page (many embassies use
    third-party scheduling systems, but the app can store the relevant
    link and instructions). This ensures the user is guided on where and
    how to schedule their visa interview after NVC documentarily
    qualifies their case. Additionally, Gateway can scrape or prompt the
    user for **interview appointment details** (date, time, and
    location) once scheduled, which it will then use for calendar
    integration (see Calendar Integration section).

-   **Visa Bulletin Updates:** For immigrants in backlogged visa
    categories (where there are annual caps by category/country), the
    **Visa Bulletin** is crucial. Gateway will incorporate data from the
    **monthly Visa Bulletin** published by the State
    Department[\[9\]](https://travel.state.gov/content/travel/en/legal/visa-law0/visa-bulletin/2025/visa-bulletin-for-august-2025.html#:~:text=A,IMMIGRANT%20VISAS)[\[10\]](https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition/step-2-begin-nvc-processing.html#:~:text=Number%20of%20Visas%20Each%20Year,is%20Limited%20in%20Some%20Categories).
    This bulletin lists the "priority dates" that are currently being
    processed for each category. The app can either scrape the HTML of
    the monthly bulletin or use any available data source to determine
    if a user's priority date is current. For instance, if a user is an
    applicant in the F2B family category (unmarried adult child of a
    permanent resident), the app will know their filing priority date
    (which the user can input or it can derive from USCIS case data) and
    compare it against the latest Visa Bulletin cut-off date for F2B. If
    the date is approaching or becomes current, Gateway will notify the
    user that their visa number may become available. This way, users
    are kept informed about **visa availability** and can prepare for
    the next steps (such as filing an **Adjustment of Status** if they
    are in the U.S., or getting ready for consular processing if
    abroad). By integrating the Visa Bulletin, Gateway covers the
    otherwise confusing waiting period where a petition is approved but
    cannot move forward due to quota
    backlogs[\[10\]](https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition/step-2-begin-nvc-processing.html#:~:text=Number%20of%20Visas%20Each%20Year,is%20Limited%20in%20Some%20Categories).

**5. Other DHS/ICE Data:** While USCIS handles benefits and State
handles visas, the app will also consider relevant **ICE (Immigration
and Customs Enforcement)** or other DHS services that can assist the
immigrant. One example is the **SEVP Portal/RTI (Student and Exchange
Visitor Information System)** for international students -- if Gateway
is extended to include student visa holders, it could incorporate SEVIS
data (though SEVIS has restricted access APIs for schools, so this may
be future scope). Another example is **I-94 travel history** from U.S.
Customs and Border Protection: after a user enters the U.S. on an
immigrant visa or any status, they receive an I-94 record of entry.
Gateway can link to the CBP's online I-94 retrieval tool, allowing users
to download their entry record which is often needed for applications.
Additionally, if a user needs to verify their immigration status for
employment or benefits, the app could connect to the **USCIS SAVE**
database or simply guide them to obtain proof (though SAVE is typically
used by agencies, not individuals). For the enforcement side (ICE),
Gateway will prioritize **legal compliance and reporting**: for
instance, reminding users of address change requirements (AR-11 form via
USCIS) or visa overstay consequences. If ICE provides any public-facing
tools (such as **check-in appointment scheduling** for certain cases or
the ability to report visa fraud), those could be integrated as well via
web links or APIs. Overall, while the primary focus is on the *legal
immigration progression*, Gateway ensures users have access to all **DHS
resources** relevant to staying in status and ultimately achieving
citizenship.

## End-to-End User Journey Features

Gateway is an **end-to-end solution** that supports users through each
milestone of immigration. Below is how the app guides the user step by
step, integrating the above services into a seamless journey:

-   **Step 1 -- Initial Guidance in Home Country:** The process begins
    with helping the prospective immigrant (and their U.S. sponsor, if
    applicable) determine *what path to take*. Gateway will include an
    interactive guide or questionnaire to identify the appropriate visa
    or immigration route (family-sponsored, employment-based, student,
    etc.). It will utilize official information from USCIS and State
    Department websites to present **eligibility criteria and
    requirements**. For example, if the user is seeking a family-based
    Green Card, the app explains that a U.S. citizen or LPR family
    member must file a Form I-130 petition on their
    behalf[\[11\]](https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition.html#:~:text=U,paper%20process%20through%20the%20mail).
    Gateway can link directly to the USCIS form page for the I-130 with
    instructions[\[12\]](https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition.html#:~:text=To%20learn%20more%20about%20USCIS,gov),
    or to the **USCIS online account** to file electronically. All
    relevant government websites (like USCIS's "How to Apply for a Green
    Card" guides or USAGov articles) will be hyperlinked in-app so the
    user can get **authoritative details**. Essentially, the app acts as
    a roadmap: from gathering required documents, to filing the initial
    petition or visa application, the user knows *what to do first*. If
    a petition by a U.S. sponsor is needed, the app will list the forms
    (e.g. I-130 or I-140) and support them through it (possibly by
    linking to USCIS's **electronic filing** or providing a checklist if
    it's paper-filed).

-   **Step 2 -- Petition Filing and Tracking:** Once the user (or their
    sponsor) submits the initial petition to USCIS, Gateway's job is to
    track it and inform the user. The app will prompt the user to input
    their **USCIS receipt number** (from the I-797C Notice of Action
    they receive). Using the **Case Status API**, the app starts
    monitoring the
    status[\[2\]](https://developer.uscis.gov/apis#:~:text=Case%20Status%20API).
    The status is displayed on the user's dashboard with a description
    (for example: "**Case Was Received** on X date" or "**Interview Was
    Scheduled** -- USCIS will mail you an appointment notice"). Gateway
    will parse these status messages and, where applicable, provide
    additional guidance. For instance, if the status is "Request for
    Evidence (RFE) sent," the app will alert the user to check their
    mail and respond by the deadline, explaining what an RFE means. This
    **proactive guidance** is possible because the app not only shows
    the status from USCIS but adds context about what the user should do
    next. Throughout this stage, the user can refer to the app's
    **progress checklist** -- e.g., "Petition filed ✅ (tracking in
    progress)", "Biometrics appointment ⏳ (waiting to be scheduled)",
    etc., which updates automatically based on status changes. By
    focusing on the official USCIS updates, Gateway keeps the user fully
    informed of their case's movement through the USCIS pipeline,
    reducing uncertainty.

-   **Step 3 -- National Visa Center (NVC) and Consular Processing:** If
    the immigration path requires an overseas consular interview (common
    for family and employment immigrant visas when the beneficiary is
    abroad), Gateway seamlessly transitions the user to the **NVC
    stage**. As soon as USCIS approval is detected (status moves to e.g.
    "Case Approved" or an approval notice is received), the app will
    display a new set of steps: "USCIS approved your petition. Next:
    Consular processing with the Department of State." It will explain
    that the case is being sent to the
    NVC[\[7\]](https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition/step-2-begin-nvc-processing.html#:~:text=After%20USCIS%20approves%20your%20petition%2C,messages%2C%20and%20manage%20your%20case)
    and that the user will get a **welcome letter** with instructions to
    log into the Consular Electronic Application Center
    (CEAC)[\[13\]](https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition/step-2-begin-nvc-processing.html#:~:text=step%20in%20this%20processing%20is,messages%2C%20and%20manage%20your%20case).
    Gateway will assist in this stage by: (a) providing a **direct link
    to CEAC** along with the NVC case number (once the user provides or
    the app obtains it from the welcome letter email), (b) guiding the
    user through paying visa fees, filling out the DS-260 immigrant visa
    application, and uploading required documents (the app can't perform
    these actions via API, but it can embed the web portal or checklist
    the steps, ensuring the user doesn't get lost). The app will also
    integrate the **NVC case status check** -- via CEAC's status check
    page -- to show if the case is *waiting for documents*, *in review*,
    or *interview scheduled*. By entering the CEAC case number and an
    identification (like invoice ID or principal applicant info),
    Gateway can fetch the **visa case status** (for example, "Case
    Ready", "Interview Scheduled on X date", "Issued", or
    "Administrative Processing") and display it. This saves the user
    from having to navigate the CEAC interface repeatedly. Importantly,
    when an **interview is scheduled**, the app will immediately notify
    the user and present the appointment details (date, time, and
    location of the embassy). This triggers the creation of a **calendar
    event (ICS)** and preparation tips for the interview (documents to
    carry, medical exam instructions, etc. -- drawn from the embassy's
    guidance). Essentially, Gateway acts as a personal assistant through
    the **consular processing** phase, aggregating all necessary info
    (NVC notices, consular instructions, appointment status) into one
    timeline.

-   **Step 4 -- Travel and U.S. Entry:** After a successful visa
    interview, users will get their visa stamp and can travel to the
    U.S. Gateway will update the case status to "Immigrant visa issued"
    and then guide the user on the next steps upon arrival. For
    instance, the app will remind the user that they must pay the USCIS
    Immigrant Fee (for green card production) if not already paid -- it
    can link to the USCIS online payment portal for this fee. Once the
    user enters the U.S., Gateway can help retrieve their **I-94 arrival
    record** (by linking to the CBP I-94 website where the user enters
    their passport info to get the electronic I-94). This is useful as
    proof of lawful entry. The app can also prompt the user to
    **register for a Social Security Number** (if they didn't receive
    one automatically via the immigrant visa process) and provide
    resources on settling (USCIS's "Welcome to the United States"
    guide[\[14\]](https://www.uscis.gov/sites/default/files/document/guides/M-618.pdf#:~:text=,specific%20and%20detailed%20information%2C)).
    At this juncture, the user likely becomes a **lawful permanent
    resident (LPR)** upon entry (if they had an immigrant visa). Gateway
    will mark the milestone -- perhaps showing "Congratulations, you are
    now a permanent resident!" -- and continue to track the final steps
    of that particular case, such as the delivery of the physical green
    card. Using the USCIS Case Status API, the app will catch updates
    like "Card Was Mailed" and notify the user to check their mailbox.
    This ensures they promptly receive their Green Card. During this
    period, Gateway also educates the user on LPR responsibilities (like
    carrying proof of status, renewing the Green Card in 10 years, etc.)
    and begins the next countdown: **eligibility for naturalization**.

-   **Step 5 -- Transition to Citizenship (Naturalization):** Gateway's
    ultimate goal is to see the user through to U.S. citizenship, if
    that is their aim. The app will calculate the date when the user
    becomes eligible to apply for naturalization (usually 5 years as an
    LPR, or 3 years if married to a U.S. citizen, etc., factoring in
    continuous residence rules). It will show a ticker like "*You will
    be eligible to apply for citizenship on \[date\]*" and list any
    additional requirements (e.g. physical presence, state residency).
    When the date is near, the app will alert the user and outline the
    process to file **Form N-400 (Application for Naturalization)**.
    Gateway again prioritizes official channels: it will encourage
    online filing of N-400 via the USCIS account system (with a link) or
    provide a PDF form and instructions from USCIS.gov. Once the N-400
    is filed, the app adds it to the **Case Status tracker** (just like
    before, using the receipt number). All subsequent stages --
    biometrics appointment, interview, oath ceremony -- are managed and
    messaged through Gateway. For example, when the N-400 case status
    changes to "Interview Scheduled", the app will retrieve the
    interview date from either the status description or the user's
    USCIS account (if accessible) and then help the user prepare. It
    might link to study materials for the civics test (USCIS provides a
    set of 100 questions and other resources) and create a study
    checklist. On the day of the interview, the app sends a reminder
    (this event will be on the calendar via .ics as well). If the
    interview is passed, eventually the status might show "Oath Ceremony
    Scheduled" -- Gateway will then schedule that final event on the
    calendar and inform the user of any documents to bring (e.g. their
    green card for surrender). At the oath ceremony, the user finally
    becomes a U.S. citizen. Gateway can congratulate the user and
    perhaps provide guidance on post-citizenship tasks (like updating
    Social Security, applying for a U.S. passport, registering to vote,
    etc., which again can link to official sources). This completes the
    end-to-end journey: the user started abroad seeking a visa and
    finished as a citizen, with Gateway orchestrating each step in one
    consolidated experience.

Throughout all these stages, **Gateway maintains an online-only
operation** -- it fetches live data from government sources whenever
needed, so the user always sees current information. The app will store
minimal data on the device (just enough to know the user's cases and
profile) and will always pull statuses, dates, and content from the
official APIs or websites in real time when the user is connected. This
design avoids outdated information and ensures that even changes in
processes or laws (for example, new forms or fees) are reflected quickly
by virtue of using authoritative web content.

## Calendar and Notification Integration

One of Gateway's most powerful user-experience features is its
**calendar integration** using the iCalendar (*.ics*) format.
Immigration processes involve many critical dates and appointments --
missing one can cause serious delays. Gateway will make it nearly
impossible to miss a date by providing seamless calendar connectivity:

-   **Automatic .ics Event Creation:** Whenever an important event is
    scheduled in the user's immigration journey, Gateway will generate
    an industry-standard **.ics calendar event** file for that event.
    For example, if USCIS schedules a biometrics appointment on August
    10 at 2:00 PM, as soon as this is detected (via a status update or a
    USCIS notification), the app will create an "Biometrics Appointment"
    event at the specified time, including the location (address of the
    Application Support Center) and any notes (e.g. "Bring photo ID,
    appointment notice" as described in the USCIS letter). The user can
    then easily add this to their personal calendar of choice. Since
    .ics is a universal format, the event can be imported into Google
    Calendar, Apple Calendar, Outlook, or any other calendar
    app[\[15\]](https://www.gsa.gov/system/files/ImportCalendarGoogle.pdf#:~:text=,file%20has%20been%20posted%20to).
    Gateway can do this behind the scenes on Android by using calendar
    provider APIs (with the user's permission) to insert the event
    directly, or simply by downloading the .ics file and prompting the
    user to open it, which will add to their calendar. The advantage of
    using .ics is that it's widely supported and doesn't require the app
    to have deep integration with each calendar provider; it leverages
    existing calendar apps.

-   **Two-Way Sync and Updates:** The app will maintain its own list of
    upcoming immigration-related events and also sync to calendars as
    needed. If an appointment date changes (for instance, USCIS
    reschedules an interview), Gateway will update the event details and
    push an updated .ics (or update the calendar entry if it has direct
    access). This way, any change is immediately reflected for the user.
    The .ics approach also allows for static subscription -- in the
    future, we might offer a personalized **calendar feed URL** that the
    user can subscribe to, so that all their immigration events
    automatically show up in their calendar as they are added (some
    services allow subscribing to a feed that's periodically fetched,
    but this could be a later enhancement). Initially, simply providing
    the .ics files for import will cover the requirement that *"calendar
    integration with .ics will definitely be important."*

-   **Push Notifications and Reminders:** Besides calendar events,
    Gateway will implement in-app **notifications** to remind users of
    upcoming tasks or deadlines. For example, one day before an
    interview or biometrics appointment, the app will send a reminder
    notification: "📅 Reminder: You have your USCIS interview tomorrow
    at 9:00 AM. Don't forget to bring your passport and interview
    notice." This duplicates what's on the calendar, but ensures
    visibility even if the user hasn't checked their calendar.
    Notifications are also used for case status changes ("✅ Your case
    status was updated: Interview was completed, and your case is being
    reviewed.") and for prompting next steps ("✉️ USCIS has sent an RFE.
    Please respond by the deadline. Tap for details."). All
    notifications will be generated client-side based on data fetched
    from APIs, and delivered securely. Users can customize which
    notifications they want (e.g. status changes, reminders, general
    tips). **Secure channels** (Android Notification with proper privacy
    settings) will be used so that sensitive info isn't exposed on a
    lock screen unless the user allows it.

-   **Integration with Device Calendars:** On Android, Gateway can
    utilize the Calendar Provider APIs to insert events directly into
    the user's Google Calendar (or any calendar the device is synced
    with). This may require requesting calendar write permissions. An
    alternative approach is using the Google Calendar API via REST,
    which would require the user to sign in with their Google account
    for the app -- however, to keep things simple and privacy-focused,
    Gateway might avoid storing Google credentials and use the on-device
    calendar integration instead. In either case, the events added will
    include all needed info. For instance, an event for "U.S. Embassy
    Interview" will have the embassy address and maybe a URL to the
    embassy's instructions page, so the user has everything in one
    place. This combination of ICS and direct integration ensures that
    **the user's schedule of immigration-related appointments is
    seamlessly merged with their personal schedule**, reducing the
    chance of oversight.
    Gateway now requests calendar permissions at runtime, inserts events using `CalendarEvent`, schedules reminder notifications via WorkManager, and updates or cancels calendar entries automatically when appointment dates change.

-   **Time Zone and .ics Considerations:** Since the app may cater to
    users still overseas (e.g. waiting for a consular interview in their
    home country), Gateway will handle time zones carefully. It will
    generate .ics events in the **local time zone of the appointment**
    (which can be parsed from the appointment location or explicitly
    given by USCIS/embassy). The .ics file will include the time zone
    info so that when added to a calendar, it shows the correct local
    time. This is important to avoid confusion for users who may have
    their phone still set to their home country's time zone vs. U.S.
    time zone. All dates will also be confirmed in-app in a
    human-friendly way ("Your interview is on **Sep 5, 2025 at 10:00 AM
    (Local time in New Delhi)**"). These details, while small, greatly
    enhance user confidence in the app as a comprehensive organizer for
    their immigration journey.

## Security and Online-Only Architecture

Security is paramount in an application dealing with personal
immigration data. Gateway is designed to be **online-only** and fully
secure in every interaction:

-   **Secure Connections (HTTPS):** All API calls and website fetches
    performed by Gateway will use HTTPS endpoints. The importance of
    this is underscored by government requirements -- for example, USCIS
    developer APIs are only accessible via secure HTTPS
    connections[\[16\]](https://developer.uscis.gov/#:~:text=Image%3A%20Https).
    Any time Gateway connects to a USCIS or other government service, it
    does so over TLS/SSL, ensuring the data (which could include
    personal identifiers, case numbers, etc.) is encrypted in transit.
    Users will see visual cues of security when interacting with
    embedded web content (e.g. the lock icon in WebViews for government
    sites), reinforcing that **sensitive information is only shared on
    official, secure
    websites**[\[16\]](https://developer.uscis.gov/#:~:text=Image%3A%20Https).

-   **OAuth 2.0 Authentication for APIs:** Gateway will authenticate to
    government APIs using robust protocols. The USCIS Torch Platform
    APIs (Case Status, FOIA, etc.) are all secured via **OAuth 2.0 using
    the Client Credentials
    flow**[\[17\]](https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature#:~:text=Using%20the%20Torch%20API%20Sandbox,0%20client%20Credentials).
    This means the app (or the "agent" on the backend) must obtain an
    access token from USCIS by presenting its client ID and secret, and
    then use that token in API requests. The diagram below illustrates
    this flow: the app communicates with the USCIS authorization server
    to get a token, and then uses the token to call the protected API
    endpoints.

*Figure: USCIS Torch Platform OAuth 2.0 Client Credentials flow for API
access. Gateway will authenticate using a client ID/secret to obtain a
token before calling USCIS
APIs[\[17\]](https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature#:~:text=Using%20the%20Torch%20API%20Sandbox,0%20client%20Credentials).*

Implementing this, the Gateway developer/agent will first register an
application on the USCIS Developer Portal and receive a **Consumer Key
(Client ID) and Consumer
Secret**[\[18\]](https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature#:~:text=To%20begin%20development%20in%20the,Create%20an%20App%20process%20to)[\[19\]](https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature#:~:text=First%2C%20we%20will%20need%20to,a%20new%20tab%20or%20window).
In the sandbox environment, an access token endpoint (e.g.
`https://api-int.uscis.gov/oauth/accesstoken` for sandbox) is used to
get a
token[\[20\]](https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature#:~:text=The%20Access%20Token%20URL%20can,for%20Sandbox%20or%20Production%20environments).
The app will securely store the client secret and never expose it. All
token requests and API calls will include the necessary headers as per
USCIS documentation (likely an Authorization header with the Bearer
token). The token has a limited lifespan, so the app will handle
refreshing it as needed. **No USCIS API call will be made without a
valid token**, which preserves security and follows USCIS's integration
rules. Moreover, if Gateway eventually moves to production API usage, it
will undergo USCIS's security review and **demo approval process**
before being given production
credentials[\[21\]](https://developer.uscis.gov/#:~:text=3.%20). (USCIS
requires developers to demonstrate their app and adherence to rules as a
prerequisite for production API keys, including showing that only
authorized data is accessed,
etc.[\[21\]](https://developer.uscis.gov/#:~:text=3.%20).)

-   **User Data Privacy:** Gateway will handle users' personal data with
    extreme care. All personal identifiable information (PII) such as
    names, dates of birth, A-numbers, receipt numbers, etc., will be
    stored locally on the device **in encrypted form** (using Android's
    encrypted shared preferences or keystore, for example). The app will
    not transmit PII to any third-party servers; communications are only
    between the app and the official government endpoints. If the
    architecture uses a backend server (for the agent orchestration),
    that backend will also enforce encryption and never log sensitive
    data. We will follow privacy guidelines likely in line with
    government standards (similar to FedRAMP moderation if aiming to
    partner with USCIS). Users may need to log in to the app for
    identification -- this could be done purely client-side (data stored
    under a device PIN) or via a cloud account if multiple device sync
    is needed, but in any case, strong authentication (possibly
    integrating with **Login.gov** in the future) will be considered for
    user identity management. Additionally, the app will clearly inform
    users that it is not an official government app (at least until any
    official partnership) but uses **authorized APIs** to fetch data on
    the user's behalf, requiring their consent to do so.

    Gateway's Android implementation stores tokens and case numbers in
    encrypted preferences using Android's Jetpack Security library.
    Data is tied to device credentials so even if the underlying storage
    is accessed, the contents remain unreadable without unlocking the
    device. A simplified example is provided in
    `SecureStorage.kt`:

    ```kotlin
    class SecureStorage(context: Context) {
        private val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .setUserAuthenticationRequired(true,
                TimeUnit.MINUTES.toSeconds(5))
            .build()

        private val prefs = EncryptedSharedPreferences.create(
            context,
            "gateway_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    ```

-   **Online-Only Design:** By being "online-only", Gateway avoids
    storing large amounts of static data on the device. It does not
    function offline except to show whatever last synced info with a
    timestamp. This design ensures that whenever the user takes an
    action or needs an update, the app is pulling fresh data from the
    source. It prevents stale or incorrect information from misleading
    the user. For example, instead of caching the status of a case
    indefinitely, the app fetches it anew each time the user opens the
    app (or at regular background intervals) so that the user is seeing
    the latest update from USCIS. All form instructions or checklists
    are likewise fetched or updated from official web sources, so
    changes in fees or policy will be reflected quickly. This approach,
    however, means the app needs reliable internet; given the target
    users (who will typically have internet when dealing with online
    immigration systems), this trade-off is acceptable. To secure the
    communication further, we will implement checks like **certificate
    pinning** for API domains to mitigate any risk of man-in-the-middle
    attacks, and use up-to-date TLS protocols as mandated by federal IT
    standards.

-   **Secure Handling of Calendars and Emails:** The calendar
    integration (if using device calendars or Google API) will be done
    with minimal access. If the user opts to allow direct calendar
    access, we will only insert events related to the app's purpose. The
    .ics files do not contain extraneous personal data beyond what's
    necessary (like we won't put a full name or A-number in the calendar
    description, to avoid sensitive info leaking if a calendar is
    shared; instead, we use generic labels like "USCIS Interview"
    without exposing ID numbers). Similarly, if the app offers to parse
    emails (for example, NVC emails or USCIS notifications) to
    automatically update status, it will do so only with the user's
    explicit permission and possibly using a local ML model or secure
    API, ensuring email credentials are not stored. At MVP, this email
    parsing may not be included, but it's an idea for convenience (e.g.,
    user links their email for any USCIS notices and the app can detect
    new notices).

In summary, security in Gateway is not an afterthought but a
foundational aspect. Using **OAuth 2.0, HTTPS, encryption, and
compliance with government data handling policies**, the app will
maintain the trust required for users to confidently manage their
immigration journey online. Every integrated service is through official
channels -- no screen-scraping of private data behind logins (except
perhaps read-only status checks where no login exists) -- and as Gateway
moves to production, it will coordinate closely with USCIS to ensure all
security and privacy expectations are met.

## Implementation Plan and Development Roadmap

Building Gateway involves orchestrating multiple APIs and components.
Below is a high-level implementation plan for the developer/agent,
ensuring all pieces come together:

1.  **Set Up USCIS Developer Accounts:** Start by registering on the
    **USCIS Developer Portal (Torch Platform)** and create a Developer
    App[\[22\]](https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature#:~:text=Step%201%3A%20Create%20your%20App,s).
    This will provide the **Client ID and Secret** needed for accessing
    USCIS APIs. Initially, use the Sandbox environment for testing API
    calls. Confirm the ability to obtain OAuth2 access tokens from the
    sandbox auth
    endpoint[\[20\]](https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature#:~:text=The%20Access%20Token%20URL%20can,for%20Sandbox%20or%20Production%20environments).
    Enable the required API products (Case Status, FOIA) for the app in
    the developer portal
    settings[\[23\]](https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature#:~:text=,result%20in%20Sandbox%20Authentication%20Errors).
    Test the connectivity by using tools like Postman or the portal's
    "Try It" feature to call a sample case status with a dummy receipt
    number in sandbox, ensuring that the authentication flow works and
    the format of responses is understood (e.g., JSON structure of case
    status).

2.  **Develop Core API Integration Modules:** Implement the Android (or
    backend) modules to interact with each API:

3.  The **USCIS Case Status API module** should handle constructing
    requests with a receipt number and parsing the response. Likely, the
    endpoint will be something like
    `GET /v1/case-status/{receiptNumber}` or a query param. It will
    return status code (like an internal code for each status) and a
    status message. Design this module to be easily callable (e.g. a
    function `checkCaseStatus(receipt)` that returns a status object).
    Include error handling for cases like "invalid receipt" or API
    downtime.

4.  The **USCIS FOIA API module** will handle two operations: creating a
    FOIA request and checking FOIA status. The "create" operation will
    require gathering user data (like name, DOB, A-number, etc.) and
    packaging it in the required JSON payload as per FOIA API spec. The
    module should then POST to the FOIA API endpoint and retrieve a FOIA
    case number from the response. The status check operation will
    accept that FOIA case number and GET the current status (e.g.
    pending, completed, ready for download, etc.). We will build a
    simple UI in the app for users to enter FOIA details if they choose
    to use this feature, and display the status/provide download link if
    records are ready.

5.  The **Processing Times module** will call the USCIS Processing Times
    API. Since this is not part of the official Torch APIs, it might not
    need the OAuth token; it could be an open GET request. We will
    confirm by observing network calls from the USCIS website. Implement
    this as needed: possibly a sequence of calls (one to list forms, one
    to list subtypes, one to list field offices, and one to get times,
    as indicated by the R script
    functions[\[24\]](https://rd.thecoatlessprofessor.com/uscis-processing/#:~:text=The%20package%20provides%20a%20set,overview%20of%20the%20functions%20available)).
    We can simplify if we only need specific forms (like just get the
    time for the form relevant to the user's case).

6.  The **State Department integration** doesn't have official APIs, so
    implementation involves automating interactions:
    -   For **CEAC Status**: likely a POST request to a status check
        page with form fields (visa type, case number) and parsing the
        HTML of the result. Write a helper that can parse the response
        (which might say "Your case is in transit" or "Issued" in text).
        This requires careful coding and maybe using an HTML parser
        library since it's not JSON. We'll test this with known sample
        case numbers.
    -   For NVC, since everything is via the CEAC portal, we might not
        automate beyond status. We will provide a WebView for the CEAC
        login for the user to manage their documents -- thus minimal
        backend coding there except embedding the site.
    -   For **Visa Bulletin**: write a small parser that can fetch the
        latest Visa Bulletin page (or perhaps use an RSS feed if
        available, or an unofficial API from travel.state.gov).
        Alternatively, maintain a JSON of priority date cutoffs per
        month (somebody might have an API, or we parse the HTML
        ourselves). In MVP, we could even hard-code or manually update a
        small dataset of current bulletin info, given the bulletin is
        monthly.

7.  The **Calendar/Notification module**: Use Android's AlarmManager or
    WorkManager to schedule checks for upcoming events and trigger
    notifications. Implement .ics file generation (which is essentially
    writing a text file in iCalendar format) or use a library if
    available. Also implement the logic to insert events into the device
    calendar (via Calendar Provider intents).

8.  **Design the User Interface for Clarity:** Following the user's
    needs, design the UI with logical sections for each stage of the
    process:

9.  A **timeline dashboard** that shows the user's overall progress
    (Petition -\> Approval -\> NVC -\> Visa -\> Entry -\> Green Card -\>
    Citizenship) with some steps check-marked as completed and the
    current step highlighted. This gives a quick overview of where the
    user is in the journey.

10. A **case detail screen** for each application (e.g., if the user has
    an I-130 and an I-485 in process, each has its own detail view)
    showing status history and latest update.

11. A **documents/tasks checklist** where users can see what tasks they
    need to do (upload birth certificate, attend medical exam, etc.).
    This could be dynamically generated from templates once we know the
    visa category.

12. **Notification center** or messages view to review all alerts the
    app has given (so nothing is missed).

13. The UI should also incorporate a **calendar view** or integration,
    e.g. a page that shows upcoming appointments on a calendar within
    the app for quick reference.

14. Ensure that throughout the UI, there are info buttons or links that
    open official guidance in-app (like small "i" icons next to complex
    terms, which when tapped show a definition or link to USCIS/State
    FAQ pages).

15. **Testing with Sample Data:** Use realistic (but fictional) test
    cases to run through the entire flow. For example, simulate a
    family-based immigration case: enter an I-130 receipt number, use
    the sandbox API to get a dummy status sequence, simulate an
    approval, then manually input a fake NVC case number, ensure the
    State Dept status parser works with a known test case (some embassy
    might provide a test visa status, or use a recently completed case
    if available with permission). Test the .ics generation by
    scheduling some dummy events and importing them into Google Calendar
    to see that times and details align. Security testing is also
    crucial -- for instance, verify that if the device is offline, the
    app handles it gracefully (maybe show last known info with a warning
    it might be outdated).

16. **Iterate and Refine:** Incorporate user feedback or agent testing
    feedback. Perhaps the FOIA feature is niche and could be hidden
    under advanced settings if it clutters the UI. Or maybe users want
    the app to also track non-immigrant visa status (which we could
    extend to doing via CEAC for NIV cases). Keep the design modular so
    new API integrations can be added. For example, if USCIS releases a
    new API for **Infopass appointments or field office appointments**,
    we should be able to plug that in.

17. **Security Review and Go Live:** Once the MVP is functional in
    sandbox mode, plan for production deployment. This involves
    contacting USCIS to get production API access. USCIS likely will
    require a demonstration of the app and meeting their **production
    access
    checklist**[\[21\]](https://developer.uscis.gov/#:~:text=3.%20).
    We'll ensure all their criteria (like not exceeding rate limits,
    handling errors properly, using real data responsibly, etc.) are
    met. After approval, switch the API base URLs from sandbox to
    production, test again with real cases (possibly the developer's own
    case or volunteers), and prepare for launch. The app will be
    released likely as a pilot/beta initially.

Throughout development, we utilize **OpenAI's Codex** and other
AI-assisted coding tools to accelerate writing boilerplate and ensure
correctness. For instance, Codex can help generate functions to parse
JSON from the Case Status API or create the ICS file structure. It can
also assist in writing complex parsing logic for the Visa Bulletin HTML.
By leveraging AI coding assistance, development becomes faster and less
error-prone, allowing the team to focus more on design and integration
logic.

## Future Scope and USCIS Partnership

Upon achieving a successful MVP with the above features, the scope can
be expanded, and a formal **pitch to USCIS and other stakeholders** will
be prepared. Some future enhancements and collaboration points include:

-   **Official USCIS Partnership:** The pitch to USCIS will highlight
    how Gateway aligns with their goals of digital transformation. By
    demonstrating the working MVP, we can show that using USCIS's own
    APIs in a creative way greatly improves user experience. We'll
    emphasize that Gateway could help *"transform their business"* by
    engaging more users digitally and reducing manual inquiries, as the
    Torch platform
    intended[\[25\]](https://constacloud.com/uscis-gov-api-integration-services.html#:~:text=match%20at%20L1085%20applications%20to,increase%20transparency%2C%20and%20more%20efficiently).
    Specifically, the benefits to USCIS include fewer status inquiries
    to call centers (since users get updates on the app) and more
    informed applicants (which can lead to fewer mistakes in
    applications). We will seek USCIS's support to maybe officially
    endorse or integrate Gateway into their offerings, or at least get
    continued access to APIs and possibly expanded APIs (like case
    history or appointment details if they can provide them via API).

-   **Scaling to More Use Cases:** Currently, Gateway focuses on the
    common family/work immigration path and naturalization. In the
    future, it can be expanded to cover **non-immigrant visas** (like
    student or tourist visa steps), **asylum/refugee processes**, or
    other DHS interactions. Each of these might require integrating
    additional systems (for example, an asylum applicant could benefit
    from tracking their case at EOIR -- the immigration courts -- which
    is another system to consider if we go that route). The modular
    architecture will allow adding these with additional research and
    API usage where available.

-   **AI-Driven Assistance:** With OpenAI's technology, we plan to
    integrate an **AI assistant within the app** to answer users'
    questions in natural language. This could be based on GPT-4 or
    similar models fine-tuned on immigration information (within the
    bounds of accuracy and legal advice constraints). The assistant
    could help explain confusing terms, or guide the user through form
    questions by providing personalized help (e.g., "How should I answer
    the question about employment history on form X?"). This feature
    would make Gateway not just a tracker but a smart **virtual
    immigration agent**. Of course, we'd do this carefully, making sure
    the answers are sourced from official info (we can have the AI cite
    USCIS manual or policy, for example). This is an area where OpenAI's
    expertise directly contributes to the app's value.

-   **Enhanced Calendar Sync (Bi-directional):** We can integrate deeper
    with calendar services to allow the app to check for conflicts
    (e.g., warn the user if their USCIS interview clashes with another
    event on their Google Calendar). Also, sending automatic email
    invitations for events to the user (so they simply accept and it
    goes on their calendar) can be added.

-   **Collaborations with Other Agencies:** Beyond USCIS, we might pitch
    to the Department of State (for the visa stage) and even DHS's
    overall digital services. If those agencies see the benefit, they
    might provide better data access. For example, State might provide
    an API for CEAC status if they realize an app like this is pulling
    it regularly. Our outreach can include showing how Gateway improves
    compliance (users less likely to miss interviews or documents) which
    ultimately makes the agencies' job easier too.

-   **Open Data and Analytics:** Gateway will accumulate anonymized data
    on processing times and user pain points. We could use this (in
    aggregate) to identify patterns -- e.g., if many users are getting
    RFEs for the same form field, that indicates a common issue. Such
    insights could be shared (without personal data) with USCIS to help
    them improve instructions or forms. This kind of feedback loop might
    be part of the pitch to show that Gateway is not just using
    government data, but also *giving back* useful analytics to the
    agencies.

Finally, the expansion and pitch will underscore the core achievement:
**revolutionizing immigration processing** by connecting every service
into one flow. By demonstrating a functional MVP, we can make the case
that a public-private partnership or government adoption of Gateway
would greatly benefit immigrants and the agencies alike. The USCIS
Director or CIO's office would likely appreciate that we leveraged their
APIs as intended to build something that shortens timelines and
increases
transparency[\[1\]](https://constacloud.com/uscis-gov-api-integration-services.html#:~:text=will%20shorten%20decision%20timelines%2C%20increase,efficiently%20handle%20immigration%20benefits%20requests).
The hope is that, with USCIS's endorsement, Gateway could become an
official or officially-supported app, amplifying its reach to all
prospective immigrants. The road from MVP to that vision involves
continued refinement, user feedback, and alignment with government
requirements, but the foundation laid out -- using robust APIs, secure
integration, and user-centric design -- puts us on the right track to
truly **streamline the immigration journey from start to finish**.

**Sources:** The information and APIs discussed are drawn from official
USCIS and DHS resources, including the USCIS Torch Developer Portal for
Case Status and FOIA
APIs[\[2\]](https://developer.uscis.gov/apis#:~:text=Case%20Status%20API)[\[4\]](https://developer.uscis.gov/apis#:~:text=FOIA%20Request%20and%20Status%20API),
USCIS's guidance on OAuth2 security for API
access[\[17\]](https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature#:~:text=Using%20the%20Torch%20API%20Sandbox,0%20client%20Credentials),
and descriptions of the benefits of these
integrations[\[1\]](https://constacloud.com/uscis-gov-api-integration-services.html#:~:text=will%20shorten%20decision%20timelines%2C%20increase,efficiently%20handle%20immigration%20benefits%20requests).
The Department of State's procedure for immigrant visas (NVC processing
and consular interviews) is referenced from their official
travel.state.gov
content[\[7\]](https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition/step-2-begin-nvc-processing.html#:~:text=After%20USCIS%20approves%20your%20petition%2C,messages%2C%20and%20manage%20your%20case)
and visa bulletin
publications[\[10\]](https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition/step-2-begin-nvc-processing.html#:~:text=Number%20of%20Visas%20Each%20Year,is%20Limited%20in%20Some%20Categories).
All integrations will use secure, official channels as cited above to
ensure reliability and compliance.

[\[1\]](https://constacloud.com/uscis-gov-api-integration-services.html#:~:text=will%20shorten%20decision%20timelines%2C%20increase,efficiently%20handle%20immigration%20benefits%20requests)
[\[25\]](https://constacloud.com/uscis-gov-api-integration-services.html#:~:text=match%20at%20L1085%20applications%20to,increase%20transparency%2C%20and%20more%20efficiently)
USCIS API Integrations Developer \| USCIS API Custom API Integration
Development

<https://constacloud.com/uscis-gov-api-integration-services.html>

[\[2\]](https://developer.uscis.gov/apis#:~:text=Case%20Status%20API)
[\[3\]](https://developer.uscis.gov/apis#:~:text=Provides%20case%20status%20information%20to,access%20to%20case%20status%20information)
[\[4\]](https://developer.uscis.gov/apis#:~:text=FOIA%20Request%20and%20Status%20API)
API Catalog \| USCIS Developer Portal

<https://developer.uscis.gov/apis>

[\[5\]](https://rd.thecoatlessprofessor.com/uscis-processing/#:~:text=Information%20is%20obtained%20by%20making,Processing%20Time%20API%20found%20at)
[\[6\]](https://rd.thecoatlessprofessor.com/uscis-processing/#:~:text=processing%20time%20form%20makes%20to,Processing%20Time%20API%20found%20at)
[\[24\]](https://rd.thecoatlessprofessor.com/uscis-processing/#:~:text=The%20package%20provides%20a%20set,overview%20of%20the%20functions%20available)
USCIS Immigration Form Processing Times • uscis

<https://rd.thecoatlessprofessor.com/uscis-processing/>

[\[7\]](https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition/step-2-begin-nvc-processing.html#:~:text=After%20USCIS%20approves%20your%20petition%2C,messages%2C%20and%20manage%20your%20case)
[\[10\]](https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition/step-2-begin-nvc-processing.html#:~:text=Number%20of%20Visas%20Each%20Year,is%20Limited%20in%20Some%20Categories)
[\[13\]](https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition/step-2-begin-nvc-processing.html#:~:text=step%20in%20this%20processing%20is,messages%2C%20and%20manage%20your%20case)
NVC Processing

<https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition/step-2-begin-nvc-processing.html>

[\[8\]](https://ceac.state.gov/CEACStatTracker/Status.aspx?App=NIV#:~:text=CEAC%20Visa%20Status%20Check%20,Application%20ID%20or%20Case%20Number)
CEAC Visa Status Check - Consular Electronic Application Center

<https://ceac.state.gov/CEACStatTracker/Status.aspx?App=NIV>

[\[9\]](https://travel.state.gov/content/travel/en/legal/visa-law0/visa-bulletin/2025/visa-bulletin-for-august-2025.html#:~:text=A,IMMIGRANT%20VISAS)
Visa Bulletin For August 2025

<https://travel.state.gov/content/travel/en/legal/visa-law0/visa-bulletin/2025/visa-bulletin-for-august-2025.html>

[\[11\]](https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition.html#:~:text=U,paper%20process%20through%20the%20mail)
[\[12\]](https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition.html#:~:text=To%20learn%20more%20about%20USCIS,gov)
Submit a Petition

<https://travel.state.gov/content/travel/en/us-visas/immigrate/the-immigrant-visa-process/step-1-submit-a-petition.html>

[\[14\]](https://www.uscis.gov/sites/default/files/document/guides/M-618.pdf#:~:text=,specific%20and%20detailed%20information%2C)
\[PDF\] Welcome to the United States: A Guide for New Immigrants - USCIS

<https://www.uscis.gov/sites/default/files/document/guides/M-618.pdf>

[\[15\]](https://www.gsa.gov/system/files/ImportCalendarGoogle.pdf#:~:text=,file%20has%20been%20posted%20to)
\[PDF\] How to Import a ICS file by adding an URL in Google Calendar -
GSA

<https://www.gsa.gov/system/files/ImportCalendarGoogle.pdf>

[\[16\]](https://developer.uscis.gov/#:~:text=Image%3A%20Https)
[\[21\]](https://developer.uscis.gov/#:~:text=3.%20) Home \| USCIS
Developer Portal

<https://developer.uscis.gov/>

[\[17\]](https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature#:~:text=Using%20the%20Torch%20API%20Sandbox,0%20client%20Credentials)
[\[18\]](https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature#:~:text=To%20begin%20development%20in%20the,Create%20an%20App%20process%20to)
[\[19\]](https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature#:~:text=First%2C%20we%20will%20need%20to,a%20new%20tab%20or%20window)
[\[20\]](https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature#:~:text=The%20Access%20Token%20URL%20can,for%20Sandbox%20or%20Production%20environments)
[\[22\]](https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature#:~:text=Step%201%3A%20Create%20your%20App,s)
[\[23\]](https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature#:~:text=,result%20in%20Sandbox%20Authentication%20Errors)
How to test an API in Sandbox - "Try It Authorize" Feature \| USCIS
Developer Portal

<https://developer.uscis.gov/article/how-test-api-sandbox-try-it-authorize-feature>
