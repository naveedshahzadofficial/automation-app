(function(){
var verifyBtn = document.querySelector('#VerifyBtn');
var linkBtn = document.querySelector('#link-btn a');

var observer = new MutationObserver(function(mutations) {
  mutations.forEach(function(mutation) {
    if (mutation.type === 'attributes' && (mutation.attributeName === 'style' || mutation.attributeName === 'class') ) {
      // Check if the element is now visible
      if (verifyBtn !== null && verifyBtn.style.display !== 'none') {
            verifyBtn.click();
        // Disconnect the observer since it is no longer needed
        observer.disconnect();
      }
      else if (linkBtn !== null && !linkBtn.classList.contains('disabled')) {

                JSBridge.showToast("clear history in-progress...");
                JSBridge.clearHistory(true);
                JSBridge.showToast("AirplaneMode is on...");
                JSBridge.setAirplaneMode(true);
                JSBridge.showToast("AirplaneMode is off...");
                JSBridge.setAirplaneMode(false);
                linkBtn.click();

               //linkBtn.click();
              // Disconnect the observer since it is no longer needed
              observer.disconnect();
       }
    }
  });
});


if(verifyBtn !== null){
JSBridge.showToast(verifyBtn.innerHTML);
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