# under-armour-challenge
Under Armour Coding Challenge

## Application Architecture

There are three modules that are included as part of the application architecture. I will break each of
these down below.

### Network

The Network layer will contain all network interaction integration with the NYTimes Article Search API. I utilized
retrofit as an HTTP Client when interfacing with the NYTimes API. The interface utilized to generate the Retrofit API is called
NYTimesApi. I elected to utilize RXJava in conjunction with Retrofit so I can easily manage these tasks in other modules.
Since there is some additional parsing that can be done on the objects returned from the Article Search service before passing elements to
calling classes, I have also included a NYTimesManager class that will strip these unnecessary wrapper classes from network
responses and strip out any unneeded data. This class could also be utilized to perform any additional business logic needed before returning a Single to the UI. In the
network module, there is also dependency injection required to build the NYTimesManager as well as provide it later on. In order to
have dagger correctly build the application graph, we must include NetworkModule as a module in the final Component class.

#### Network Testing

The Networking layer has testing of both the NYTimesApi and NYTimesManager. When testing the NYTimesApi, we utilize a MockWebServer
on a custom instance of our NyTimesApi so all traffic will work through this server. To queue responses, we use a TestUtil class that
opens files that contain Json that directly correlates to the Json returned from the NYTimes. We are able to validate the content returned from
the NYTimes service here as well as make sure our integration stays consistent by testing the Query Parameters, base URL and request types.

### ViewModel

The ViewModel layer will contain all business logic needed to display view components in the application. We will utilize LiveData from Google's architecture
components library to notify views of updated components from any asynchronous actions, whether it be a network call or a database action.  The
View Model layer also provides a module to be included into the base Dagger component. We bind all View Models using a custom key, @ViewModelKey so that we
can utilize the map created by Dagger to inject the ViewModelFactory.

#### ArticleDetailViewModel

The ArticleDetailViewModel is used to expose specifi data from an article. This will prevent any UI classes from
managing business logic pertaining to an article. This class also exposes the article url for a customer to share
via a list of applicable applications.

#### ArticleListViewModel

The Article ListView Model contains all interactions needed to display an Article List. This view model is
responsible for interacting with the NYTimesManager to expose articles to a calling class. It will also expose
the loading state as well as network error states.

#### ArticleSharedViewModel

The article shared view model will manage any data that should be shared across multiple classes from within
the application

#### ViewModel Testing

Given the usage of Dagger to construct our ListViewModel, we will be able to easily mock the NyTimesManager when testing
using a mocking library such as Mockito or mockk. We would do this so that the scope of our view model testing is only
testing the logic contained within these view models. Since the other view models do not have any injected dependencies
and have very little in terms of external dependencies, they would be easy to test by mocking an article object for
any additional tests.

### App

The application UI components live in the app module. Here we build our dagger tree and create it in our application class
so that we can inject fragments and activities as well as viewmodels and our network implementation. We have one container
activity, the ArticleActivity, and two fragments, ArticleDetail and ArticleSearch that round out screens that are show to
the user. Also in the app module, is an AppInjector class that utilizes lifecycle callbacks to ensure that all activies
and fragments are injected at appropriate times. Finally, there is an adapter class for articles that contains a
ViewHolder class that will bind each Article to a view. To bind views in the fragments and viewholders, I used synthetic
view bindings for ease of use and simplicity.

#### ArticleSearchFragment

The ArticleSearchFragment is the home page of the application. From this page, a user can initiate a type-ahead
search to view a list of articles. This Fragment works hand in hand with the ArticleListViewModel and ArticleAdapter
for retrieving and displaying data in a list to users.

#### ArticleDetailFragment

The ArticleDetailFragment houses the UI surrounding a particular article. This class utilizes an ArticleDetailViewModel
to display relevant information about an article to users


#### Potential App Module Testing
Since the AppModule is essentially our UI container, we could utilize Espresso for testing this component. Since we use
dagger for our dependency structuring, it would be easy to add a custom component that had a different implementation
of the NYTimesApi that utilized mockwebserver so we had complete control over network responses. Not only would this
speed our tests up, it would also give us complete control over the data being utilized by the application in our espresso
suite. This would give us more confidence in our testing and help prevent any false negatives due to dynamic remote data
changing or network failures occuring.
