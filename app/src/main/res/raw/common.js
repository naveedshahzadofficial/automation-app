(function(){
var verifyBtn = document.querySelector('#VerifyBtn');
var linkBtn = document.querySelector('#link-btn a');



var observer = new MutationObserver(function(mutations) {
  mutations.forEach(function(mutation) {
    if (mutation.type === 'attributes' && (mutation.attributeName === 'style' || mutation.attributeName === 'class') ) {
      if (verifyBtn !== null && verifyBtn.style.display !== 'none') {
            verifyBtn.click();
        observer.disconnect();
      }
      else if (linkBtn !== null && !linkBtn.classList.contains('disabled')) {
              linkBtn.click();
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
 linkBtn.addEventListener("click", function() {
    JSBridge.clearHistory();
    JSBridge.showToast("AirplaneMode is on...");
    JSBridge.setAirplaneMode(true);
    JSBridge.showToast("AirplaneMode is off...");
    JSBridge.setAirplaneMode(false);
    JSBridge.setCounting();
 });
}

})();