(function(){
var verifyBtn = document.querySelector('#VerifyBtn');
JSBridge.showToast(verifyBtn.innerHTML);

var observer = new MutationObserver(function(mutations) {
  mutations.forEach(function(mutation) {
    if (mutation.type === 'attributes' && mutation.attributeName === 'style') {
      // Check if the element is now visible
      if (verifyBtn.style.display !== 'none') {
            verifyBtn.click();
        // Disconnect the observer since it is no longer needed
        observer.disconnect();
      }
    }
  });
});

observer.observe(verifyBtn, { attributes: true });

verifyBtn.addEventListener("click", function() {
 var nextBtn = document.querySelector('#NextBtn');
 //nextBtn.click();
});

})();