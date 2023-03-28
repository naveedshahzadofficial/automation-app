(function(){
var verifyBtn = document.querySelector('#VerifyBtn');
var linkBtn = document.querySelector('#link-btn a');
JSBridge.showToast(verifyBtn.innerHTML);


var observer = new MutationObserver(function(mutations) {
  mutations.forEach(function(mutation) {
    if (mutation.type === 'attributes' && (mutation.attributeName === 'style' || mutation.attributeName === 'class') ) {
      // Check if the element is now visible
      if (verifyBtn !== null && verifyBtn.style.display !== 'none') {
            verifyBtn.click();
        // Disconnect the observer since it is no longer needed
        observer.disconnect();
      }
      else if (linkBtn !== null && linkBtn.classList.contains('disabled')) {
                 //setAirplaneMode(true);
                setTimeout(function() {
                  linkBtn.click();
                }, 10000);
              // Disconnect the observer since it is no longer needed
              observer.disconnect();
       }
    }
  });
});


if(verifyBtn !== null){
observer.observe(verifyBtn, { attributes: true });
verifyBtn.addEventListener("click", function() {
setTimeout(function() {
   var nextBtn = document.querySelector('#NextBtn');
   var href = nextBtn.getAttribute('href');
   JSBridge.showToast(href);
   nextBtn.click();
   //window.location = 'https://www.example.com';
}, 5000);
});
}

if(linkBtn !== null){
 observer.observe(linkBtn, { attributes: true });
}

})();

// Define a function to turn on/off airplane mode
function setAirplaneMode(isEnabled) {
  // Check if the Android device is running in a webview
  if (typeof JSBridge !== 'undefined') {
    // Call the Android method to turn on/off airplane mode
    JSBridge.setAirplaneMode(isEnabled);
  } else {
    console.log('The function is not available outside of the Android app.');
  }
}